package oldmana.md.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.md.net.packet.server.PacketPlayerInfo;
import oldmana.md.net.packet.server.PacketPlayerStatus;
import oldmana.md.net.packet.server.PacketUndoCardStatus;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.server.ButtonManager.PlayerButton;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.collection.Bank;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.PostCardBankedEvent;
import oldmana.md.server.event.PreCardBankedEvent;
import oldmana.md.server.net.ConnectionThread;
import oldmana.md.server.status.StatusEffect;

public class Player extends Client implements CommandSender
{
	private static int nextID;
	
	private MDServer server;
	
	private int uid;
	
	private int id;
	
	private boolean op;
	
	private String name;
	
	private Hand hand;
	private Bank bank;
	private List<PropertySet> propertySets;
	
	//private List<Card> turnHistory = new ArrayList<Card>();
	
	private List<Card> revocableCards = new ArrayList<Card>();
	
	private List<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
	
	private Map<Player, PlayerButton[]> buttonPerspectives = new HashMap<Player, PlayerButton[]>();
	
	private boolean online = false;
	private int lastPing;
	private boolean sentPing = false;
	
	public Player(MDServer server, int uid, ConnectionThread net, String name, boolean op)
	{
		super(net);
		this.server = server;
		this.uid = uid;
		this.name = name;
		this.op = op;
		id = nextID++;
		
		server.broadcastPacket(new PacketPlayerInfo(getID(), getName(), true), this);
		hand = new Hand(this);
		bank = new Bank(this);
		propertySets = new ArrayList<PropertySet>();
		
		lastPing = server.getTickCount();
	}
	
	public int getUID()
	{
		return uid;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean canRevokeCard()
	{
		return !revocableCards.isEmpty();
	}
	
	public Card getLastRevocableCard()
	{
		return revocableCards.get(revocableCards.size() - 1);
	}
	
	public void addRevocableCard(Card card)
	{
		revocableCards.add(card);
		sendPacket(new PacketUndoCardStatus(card.getID()));
	}
	
	public Card popRevocableCard()
	{
		if (!revocableCards.isEmpty())
		{
			Card card = getLastRevocableCard();
			revocableCards.remove(card);
			sendUndoStatus();
			return card;
		}
		return null;
	}
	
	public void sendUndoStatus()
	{
		if (canRevokeCard())
		{
			sendPacket(new PacketUndoCardStatus(getLastRevocableCard().getID()));
		}
		else
		{
			sendPacket(new PacketUndoCardStatus(-1));
		}
	}
	
	public void undoCard()
	{
		popRevocableCard().transfer(getHand());
	}
	
	public void clearRevocableCards()
	{
		revocableCards.clear();
		sendPacket(new PacketUndoCardStatus(-1));
	}
	
	public boolean isOnline()
	{
		return online;
	}
	
	public void setOnline(boolean online)
	{
		this.online = online;
		if (online)
		{
			setLastPing(getServer().getTickCount());
			setSentPing(false);
			getServer().broadcastPacket(new PacketPlayerStatus(getID(), true), this);
		}
		else
		{
			setSentPing(false);
			if (getNet() != null)
			{
				getNet().close();
				setNet(null);
			}
			getServer().broadcastPacket(new PacketPlayerStatus(getID(), false));
		}
	}
	
	public int getLastPing()
	{
		return lastPing;
	}
	
	public void setLastPing(int lastPing)
	{
		this.lastPing = lastPing;
	}
	
	public boolean hasSentPing()
	{
		return sentPing;
	}
	
	public void setSentPing(boolean sent)
	{
		sentPing = sent;
	}
	
	public Hand getHand()
	{
		return hand;
	}
	
	public Bank getBank()
	{
		return bank;
	}
	
	public List<PropertySet> getPropertySets()
	{
		return propertySets;
	}
	
	public List<PropertySet> getPropertySets(boolean copy)
	{
		if (!copy)
		{
			return getPropertySets();
		}
		return new ArrayList<PropertySet>(getPropertySets());
	}
	
	public PropertySet getPropertySetById(int id)
	{
		for (PropertySet set : propertySets)
		{
			if (set.getID() == id)
			{
				return set;
			}
		}
		return null;
	}
	
	public PropertySet findPropertyCardOwner(CardProperty card)
	{
		for (PropertySet set : propertySets)
		{
			if (set.hasCard(card))
			{
				return set;
			}
		}
		return null;
	}
	
	public List<CardProperty> getAllPropertyCards()
	{
		List<CardProperty> cards = new ArrayList<CardProperty>();
		getPropertySets().forEach((set) -> cards.addAll(set.getPropertyCards()));
		return cards;
	}
	
	public List<Card> getAllCards()
	{
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(getHand().getCards());
		cards.addAll(getBank().getCards());
		getPropertySets().forEach((set) -> cards.addAll(set.getCards()));
		return cards;
	}
	
	public void safelyGrantProperty(CardProperty property)
	{
		if (property.isSingleColor() && hasSolidPropertySet(property.getColor()))
		{
			PropertySet set = getSolidPropertySet(property.getColor());
			property.transfer(set);
			set.checkMaxProperties();
		}
		else
		{
			PropertySet set = createPropertySet();
			property.transfer(set);
		}
	}
	
	public boolean transferPropertyCard(CardProperty card, PropertySet set)
	{
		List<PropertyColor> compatible = set.getPossibleColors();
		compatible.retainAll(card.getColors());
		if (!compatible.isEmpty())
		{
			PropertySet prevSet = findPropertyCardOwner(card);
			prevSet.transferCard(card, set);
			if (prevSet.getCardCount() == 0)
			{
				destroyPropertySet(prevSet);
			}
			return true;
		}
		return false;
	}
	
	public PropertySet transferPropertyCardNewSet(CardProperty card)
	{
		PropertySet prevSet = findPropertyCardOwner(card);
		PropertySet set = createPropertySet();
		prevSet.transferCard(card, set);
		if (prevSet.getCardCount() == 0)
		{
			destroyPropertySet(prevSet);
		}
		return set;
	}
	
	public PropertySet createPropertySet()
	{
		PropertySet set = new PropertySet(this);
		addPropertySet(set);
		return set;
	}
	
	public void destroyPropertySet(PropertySet set)
	{
		propertySets.remove(set);
		server.broadcastPacket(new PacketDestroyCardCollection(set.getID()));
	}
	
	private void addPropertySet(PropertySet set)
	{
		propertySets.add(set);
	}
	
	public void transferPropertySet(PropertySet set, Player player)
	{
		propertySets.remove(set);
		player.addPropertySet(set);
		set.setOwner(this);
	}
	
	public boolean hasSolidPropertySet(PropertyColor color)
	{
		return getSolidPropertySet(color) != null;
	}
	
	public PropertySet getSolidPropertySet(PropertyColor color)
	{
		for (PropertySet set : getPropertySets(true))
		{
			if (set.getEffectiveColor() == color && set.hasSingleColorProperty())
			{
				return set;
			}
		}
		return null;
	}
	
	public boolean hasRentableProperties(PropertyColor color)
	{
		for (PropertySet set : propertySets)
		{
			for (CardProperty card : set.getPropertyCards())
			{
				for (PropertyColor cardColor : card.getColors())
				{
					if (cardColor == color && card.isBase())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean hasRentableProperties(PropertyColor[] colors)
	{
		for (PropertyColor color : colors)
		{
			if (hasRentableProperties(color))
			{
				return true;
			}
		}
		return false;
	}
	
	public int getHighestValueRent(PropertyColor... colors)
	{
		Map<PropertyColor, Integer> colorCounts = new HashMap<PropertyColor, Integer>();
		List<PropertyColor> hasBaseColor = new ArrayList<PropertyColor>();
		for (PropertyColor color : colors)
		{
			colorCounts.put(color, 0);
		}
		for (PropertySet set : propertySets)
		{
			if (!set.hasBuildings())
			{
				for (CardProperty card : set.getPropertyCards())
				{
					for (PropertyColor color : card.getColors())
					{
						if (colorCounts.containsKey(color))
						{
							colorCounts.put(color, colorCounts.get(color) + 1);
							if (card.isBase() && !hasBaseColor.contains(color))
							{
								hasBaseColor.add(color);
							}
						}
					}
				}
			}
		}
		int highestValue = -1;
		for (Entry<PropertyColor, Integer> entry : colorCounts.entrySet())
		{
			if (hasBaseColor.contains(entry.getKey()))
			{
				highestValue = Math.max(highestValue, entry.getKey().getRent(Math.min(entry.getValue(), entry.getKey().getMaxProperties())));
			}
		}
		for (PropertySet set : propertySets)
		{
			PropertyColor color = set.getEffectiveColor();
			if (colorCounts.containsKey(color) && set.isMonopoly() && set.hasBuildings())
			{
				highestValue = Math.max(highestValue, color.getRent(color.getMaxProperties()) + set.getBuildingRentAddition());
			}
		}
		return highestValue;
	}
	
	public boolean hasAnyMonetaryAssets()
	{
		if (!getBank().isEmpty())
		{
			return true;
		}
		for (PropertySet set : getPropertySets())
		{
			for (CardProperty card : set.getPropertyCards())
			{
				if (card.getValue() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public int getTotalMonetaryAssets()
	{
		int wealth = 0;
		wealth += getBank().getTotalValue();
		for (PropertySet set : getPropertySets())
		{
			for (Card card : set.getCards())
			{
				wealth += card.getValue();
			}
		}
		return wealth;
	}
	
	public int getTotalPropertyValue()
	{
		int propValue = 0;
		for (CardProperty prop : getAllPropertyCards())
		{
			propValue += prop.getValue();
		}
		return propValue;
	}
	
	public int getMonopolyCount()
	{
		int monopolyCount = 0;
		for (PropertySet set : propertySets)
		{
			if (set.isMonopoly())
			{
				monopolyCount++;
			}
		}
		return monopolyCount;
	}
	
	public int getUniqueMonopolyCount()
	{
		List<PropertyColor> monopolyColors = new ArrayList<PropertyColor>();
		for (PropertySet set : propertySets)
		{
			if (set.isMonopoly() && !monopolyColors.contains(set.getEffectiveColor()))
			{
				monopolyColors.add(set.getEffectiveColor());
			}
		}
		return monopolyColors.size();
	}
	
	/**Checks if the hand is empty and draws 5 cards if it is.
	 * 
	 * @return True if hand was empty
	 */
	public boolean checkEmptyHand()
	{
		if (getHand().getCardCount() == 0)
		{
			clearRevocableCards();
			server.getDeck().drawCards(this, 5, 1.2);
			return true;
		}
		return false;
	}
	
	/**Get all status effects currently applied to the player
	 * 
	 * @return A copy of the list of status effects currently on the player
	 */
	public List<StatusEffect> getStatusEffects()
	{
		return new ArrayList<StatusEffect>(statusEffects);
	}
	
	/**Adds a status effect to the player
	 * 
	 * @param effect - The effect to add
	 */
	public void addStatusEffect(StatusEffect effect)
	{
		server.getEventManager().registerEvents(effect);
		statusEffects.add(effect);
	}
	
	/**Removes the status effect instance from the player
	 * 
	 * @param effect - The effect to remove
	 */
	public void removeStatusEffect(StatusEffect effect)
	{
		server.getEventManager().unregisterEvents(effect);
		statusEffects.remove(effect);
	}
	
	/**Removes all instances of a status effect type from the player
	 * 
	 * @param clazz - The type to remove
	 */
	public void removeStatusEffect(Class<? extends StatusEffect> clazz)
	{
		Iterator<StatusEffect> it = statusEffects.iterator();
		while (it.hasNext())
		{
			StatusEffect effect = it.next();
			if (effect.getClass() == clazz)
			{
				server.getEventManager().unregisterEvents(effect);
				it.remove();
			}
		}
	}
	
	public void removeStatusEffect(int index)
	{
		removeStatusEffect(statusEffects.get(index));
	}
	
	/**Gets a status effect by the type. There's no guarantee that there aren't multiple instances of a type.
	 * 
	 * @param clazz - The type to get
	 * @return The status effect
	 */
	@SuppressWarnings("unchecked")
	public <T extends StatusEffect> T getStatusEffect(Class<T> clazz)
	{
		for (StatusEffect effect : statusEffects)
		{
			if (effect.getClass() == clazz)
			{
				return (T) effect;
			}
		}
		return null;
	}
	
	public StatusEffect getStatusEffect(int index)
	{
		return statusEffects.get(index);
	}
	
	/**Check if the player has at least one instance of a given type.
	 * 
	 * @param clazz - The type to check
	 * @return Whether or not the player has one instance of the type
	 */
	public boolean hasStatusEffect(Class<? extends StatusEffect> clazz)
	{
		return getStatusEffect(clazz) != null;
	}
	
	public void clearStatusEffects()
	{
		Iterator<StatusEffect> it = statusEffects.iterator();
		while (it.hasNext())
		{
			StatusEffect effect = it.next();
			server.getEventManager().unregisterEvents(effect);
			it.remove();
		}
	}
	
	protected void createButtons(Player player)
	{
		PlayerButton[] buttons = new PlayerButton[3];
		for (int i = 0 ; i < buttons.length ; i++)
		{
			buttons[i] = new PlayerButton(this, player, i);
		}
		buttonPerspectives.put(player, buttons);
	}
	
	protected void removeButtons(Player player)
	{
		buttonPerspectives.remove(player);
	}
	
	public PlayerButton[] getButtonsFor(Player player)
	{
		return buttonPerspectives.get(player);
	}
	
	public PlayerButton getButtonView(Player player, int index)
	{
		return buttonPerspectives.get(player)[index];
	}
	
	public void clearActionButtons()
	{
		clearButtons(0);
	}
	
	public void clearButtons(int index)
	{
		for (PlayerButton[] bs : buttonPerspectives.values())
		{
			bs[0].setBlank();
		}
	}
	
	public void clearAllButtons()
	{
		for (PlayerButton[] bs : buttonPerspectives.values())
		{
			for (PlayerButton b : bs)
			{
				b.setBlank();
			}
		}
	}
	
	public void sendButtonPackets(int index)
	{
		for (Entry<Player, PlayerButton[]> entry : buttonPerspectives.entrySet())
		{
			PlayerButton[] bs = entry.getValue();
			
			bs[index].sendUpdate();
		}
	}
	
	public void sendButtonPackets()
	{
		for (Entry<Player, PlayerButton[]> entry : buttonPerspectives.entrySet())
		{
			PlayerButton[] bs = entry.getValue();
			
			for (int i = 0 ; i < bs.length ; i++)
			{
				bs[i].sendUpdate();;
			}
		}
	}
	
	public void draw()
	{
		server.getDeck().drawCards(this, 2);
		server.getGameState().markDrawn();
		server.getGameState().nextNaturalActionState();
	}
	
	public void playCardBank(Card card)
	{
		PreCardBankedEvent preEvent = new PreCardBankedEvent(this, card);
		server.getEventManager().callEvent(preEvent);
		if (!preEvent.isCanceled())
		{
			getHand().transferCard(card, getBank());
			addRevocableCard(card);
			PostCardBankedEvent postEvent = new PostCardBankedEvent(this, card);
			server.getEventManager().callEvent(postEvent);
			checkEmptyHand();
			
			server.getGameState().decrementTurn();
		}
		else
		{
			server.getGameState().resendActionState(this);
		}
	}
	
	public Packet[] getPropertySetPackets()
	{
		Packet[] packets = new Packet[propertySets.size()];
		for (int i = 0 ; i < packets.length ; i++)
		{
			packets[i] = propertySets.get(i).getCollectionDataPacket();
		}
		return packets;
	}
	
	public Packet getInfoPacket()
	{
		return new PacketPlayerInfo(getID(), getName(), isConnected());
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(new PacketChat(message));
	}
	
	@Override
	public boolean isOp()
	{
		return op;
	}
	
	public void setOp(boolean op)
	{
		this.op = op;
	}
	
	protected MDServer getServer()
	{
		return server;
	}
}
