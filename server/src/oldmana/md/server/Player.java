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
import oldmana.md.net.packet.server.PacketUndoCardStatus;
import oldmana.md.net.packet.universal.PacketChat;
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
import oldmana.md.server.util.IDCounter;

public class Player extends Client implements CommandSender
{
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
		id = IDCounter.nextPlayerID();
		
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
		for (PropertySet set : getPropertySets())
		{
			cards.addAll(set.getPropertyCards());
		}
		return cards;
	}
	
	public List<Card> getAllCards()
	{
		List<Card> cards = new ArrayList<Card>();
		cards.addAll(getHand().getCards());
		cards.addAll(getBank().getCards());
		for (PropertySet set : getPropertySets())
		{
			cards.addAll(set.getCards());
		}
		return cards;
	}
	
	/*
	public PropertySet safelyGrantProperty(CardProperty card)
	{
		CardCollection owner = card.getOwningCollection();
		PropertySet newSet = null;
		if (card.isSingleColor())
		{
			for (PropertySet set : getPropertySets(true))
			{
				for (CardProperty property : set.getPropertyCards())
				{
					if (property.isSingleColor() && property.getColor() == card.getColor())
					{
						owner.transferCard(card, set);
						newSet = set;
						
						if (set.getCardCount() > set.getEffectiveColor().getMaxProperties())
						{
							for (CardProperty prop : set.getPropertyCards())
							{
								if (!prop.isSingleColor())
								{
									transferPropertyCardNewSet(prop);
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		else
		{
			newSet = transferPropertyCardNewSet(card);
		}
		return newSet;
	}
	*/
	
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
		int highestValue = -1;
		for (Entry<PropertyColor, Integer> entry : colorCounts.entrySet())
		{
			if (hasBaseColor.contains(entry.getKey()))
			{
				int value = entry.getKey().getRent(Math.min(entry.getValue(), entry.getKey().getMaxProperties()));
				if (value > highestValue)
				{
					highestValue = value;
				}
			}
		}
		return highestValue;
	}
	
	public boolean hasAnyMonetaryAssets()
	{
		boolean broke = true;
		if (getBank().isEmpty())
		{
			SetIter:
			for (PropertySet set : getPropertySets())
			{
				for (CardProperty card : set.getPropertyCards())
				{
					if (card.getValue() > 0)
					{
						broke = false;
						break SetIter;
					}
				}
			}
		}
		else
		{
			broke = false;
		}
		return !broke;
	}
	
	public int getTotalMonetaryAssets()
	{
		int wealth = 0;
		for (Card card : getBank().getCards())
		{
			wealth += card.getValue();
		}
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
	
	public List<StatusEffect> getStatusEffects()
	{
		return new ArrayList<StatusEffect>(statusEffects);
	}
	
	public void addStatusEffect(StatusEffect effect)
	{
		server.getEventManager().registerEvents(effect);
		statusEffects.add(effect);
	}
	
	public void removeStatusEffect(StatusEffect effect)
	{
		server.getEventManager().unregisterEvents(effect);
		statusEffects.remove(effect);
	}
	
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
}
