package oldmana.md.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketDestroyButton;
import oldmana.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.md.net.packet.server.PacketKick;
import oldmana.md.net.packet.server.PacketPlayerInfo;
import oldmana.md.net.packet.server.PacketPlayerStatus;
import oldmana.md.net.packet.server.PacketUndoCardStatus;
import oldmana.md.net.packet.universal.PacketChat;
import oldmana.md.server.ClientButton.ButtonTag;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;
import oldmana.md.server.card.collection.Bank;
import oldmana.md.server.card.collection.Hand;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.event.PostActionCardPlayedEvent;
import oldmana.md.server.event.PostCardBankedEvent;
import oldmana.md.server.event.PreActionCardPlayedEvent;
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
	
	private Map<Player, List<ClientButton>> buttons = new HashMap<Player, List<ClientButton>>();
	
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
	
	public String getDescription()
	{
		return name + " (ID: " + id + ")";
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
		Card card = popRevocableCard();
		card.transfer(getHand());
		System.out.println(getName() + " undos " + card.getName());
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
	
	/**
	 * Register a button that this player will see on the provided Player.
	 * @param button The button to register
	 * @param view The player the button will be shown on
	 */
	public void registerButton(ClientButton button, Player view)
	{
		getButtonsFor(view).add(button);
		button.setOwner(this);
		button.setView(view);
		button.sendUpdate();
	}
	
	public void removeButton(ClientButton button)
	{
		getButtonsFor(button.getView()).remove(button);
		sendPacket(new PacketDestroyButton(button.getID()));
	}
	
	public List<ClientButton> getButtonsFor(Player view)
	{
		return buttons.computeIfAbsent(view, key -> new ArrayList<ClientButton>());
	}
	
	public ClientButton getButton(int id)
	{
		for (List<ClientButton> view : buttons.values())
		{
			for (ClientButton button : view)
			{
				if (button.getID() == id)
				{
					return button;
				}
			}
		}
		return null;
	}
	
	public ClientButton getButton(Player view, ButtonTag tag)
	{
		for (ClientButton button : getButtonsFor(view))
		{
			if (button.getTag() == tag)
			{
				return button;
			}
		}
		return null;
	}
	
	/**
	 * Button tags do not have guaranteed unique names, so this method of getting buttons is discouraged.
	 */
	public ClientButton getButton(Player view, String tagName)
	{
		for (ClientButton button : getButtonsFor(view))
		{
			if (button.getTag() != null && button.getTag().getName().equals(tagName))
			{
				return button;
			}
		}
		return null;
	}
	
	public boolean hasButton(Player view, ButtonTag tag)
	{
		return getButton(view, tag) != null;
	}
	
	protected void removeButtons(Player player)
	{
		if (buttons.containsKey(player))
		{
			for (ClientButton button : buttons.get(player))
			{
				sendPacket(new PacketDestroyButton(button.getID()));
			}
			buttons.remove(player);
		}
	}
	
	public void clearAllButtons()
	{
		for (ClientButton button : getAllButtons())
		{
			button.remove();
		}
	}
	
	public void sendButtonPackets()
	{
		for (ClientButton button : getAllButtons())
		{
			button.sendUpdate();
		}
	}
	
	public List<ClientButton> getAllButtons()
	{
		List<ClientButton> all = new ArrayList<ClientButton>();
		for (List<ClientButton> view : buttons.values())
		{
			all.addAll(view);
		}
		return all;
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
			
			System.out.println(getName() + " banks " + card.getName());
		}
		else
		{
			server.getGameState().resendActionState(this);
		}
	}
	
	public void playCardProperty(CardProperty property, int setID)
	{
		if (setID > -1)
		{
			PropertySet set = getPropertySetById(setID);
			getHand().transferCard(property, set);
		}
		else
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
				getHand().transferCard(property, set);
			}
		}
		addRevocableCard(property);
		
		server.getGameState().decrementTurn();
		
		System.out.println(getName() + " plays property " + property.getName());
		
		checkEmptyHand();
	}
	
	public void playCardProperty(CardProperty property, PropertySet set)
	{
		playCardProperty(property, set == null ? -1 : set.getID());
	}
	
	public void playCardAction(CardAction card)
	{
		PreActionCardPlayedEvent event = new PreActionCardPlayedEvent(this, card);
		server.getEventManager().callEvent(event);
		if (event.isCanceled())
		{
			return;
		}
		card.transfer(server.getDiscardPile());
		if (card.clearsRevocableCards())
		{
			clearRevocableCards();
		}
		if (card.isRevocable())
		{
			addRevocableCard(card);
		}
		server.getGameState().decrementTurn();
		card.playCard(this);
		
		System.out.println(getName() + " plays action card " + card.getName());
		
		server.getEventManager().callEvent(new PostActionCardPlayedEvent(this, card));
		
		checkEmptyHand();
	}
	
	public void executeCommand(String raw)
	{
		server.getCommandHandler().executeCommand(this, raw);
	}
	
	public void chat(String msg)
	{
		server.broadcastMessage(getName() + ": " + msg, true);
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
	
	/**
	 * Closes the connection with the player. This does not remove them from the game.
	 */
	public void disconnect()
	{
		disconnect("Disconnected by server");
	}
	
	/**
	 * Closes the connection with the player for a given reason. This does not remove them from the game.
	 * 
	 * @param reason - The message displayed on the client when disconnected
	 */
	public void disconnect(String reason)
	{
		sendPacket(new PacketKick(reason));
		setOnline(false);
		System.out.println("Player " + getName() + " (ID: " + getID() + ") was disconnected for '" + reason + "'");
	}
	
	protected MDServer getServer()
	{
		return server;
	}
}
