package oldmana.general.md.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oldmana.general.md.net.packet.server.PacketDestroyCardCollection;
import oldmana.general.md.net.packet.server.PacketPlayerInfo;
import oldmana.general.md.net.packet.server.PacketUndoCardStatus;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.md.server.card.CardProperty.PropertyColor;
import oldmana.general.md.server.card.collection.Bank;
import oldmana.general.md.server.card.collection.Hand;
import oldmana.general.md.server.card.collection.PropertySet;
import oldmana.general.md.server.net.ConnectionThread;
import oldmana.general.md.server.util.IDCounter;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class Player extends Client implements CommandSender
{
	private MDServer server;
	
	private int uid;
	
	private int id;
	
	private String name;
	
	private Hand hand;
	private Bank bank;
	private List<PropertySet> propertySets;
	
	private List<Card> turnHistory = new ArrayList<Card>();
	
	private List<Card> revokableCards = new ArrayList<Card>();
	
	private boolean loggedIn = false;
	
	public Player(MDServer server, int uid, ConnectionThread net, String name)
	{
		super(net);
		this.server = server;
		this.uid = uid;
		this.name = name;
		id = IDCounter.nextPlayerID();
		
		server.broadcastPacket(new PacketPlayerInfo(getID(), getName(), true), this);
		hand = new Hand(this);
		bank = new Bank(this);
		propertySets = new ArrayList<PropertySet>();
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
		return !revokableCards.isEmpty();
	}
	
	public Card getLastRevokableCard()
	{
		return revokableCards.get(revokableCards.size() - 1);
	}
	
	public void addRevokableCard(Card card)
	{
		revokableCards.add(card);
		sendPacket(new PacketUndoCardStatus(card.getID()));
	}
	
	public Card popRevokableCard()
	{
		Card card = getLastRevokableCard();
		revokableCards.remove(card);
		if (canRevokeCard())
		{
			sendPacket(new PacketUndoCardStatus(getLastRevokableCard().getID()));
		}
		else
		{
			sendPacket(new PacketUndoCardStatus(-1));
		}
		return card;
	}
	
	public void undoCard()
	{
		popRevokableCard().transfer(getHand());
	}
	
	public void clearRevokableCards()
	{
		revokableCards.clear();
		sendPacket(new PacketUndoCardStatus(-1));
	}
	
	public boolean isLoggedIn()
	{
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
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
	
	public List<Card> getAllCards()
	{
		List<Card> cards = new ArrayList<Card>();
		for (Card card : getBank().getCards())
		{
			cards.add(card);
		}
		for (PropertySet set : getPropertySets())
		{
			for (Card card : set.getCards())
			{
				cards.add(card);
			}
		}
		for (Card card : getHand().getCards())
		{
			cards.add(card);
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
	
	public boolean canRevokeCard(Card card)
	{
		for (Card turn : turnHistory)
		{
			if (card == turn)
			{
				if (card.isRevocable())
				{
					return true;
				}
			}
			else
			{
				if (turn.marksPreviousUnrevocable())
				{
					break;
				}
			}
		}
		return false;
	}
	
	// TODO: Mess with turn count
	public void revokeCard(Card card)
	{
		if (canRevokeCard(card))
		{
			if (card instanceof CardProperty)
			{
				for (PropertySet set : propertySets)
				{
					if (set.hasCard(card))
					{
						set.transferCard(card, hand);
						break;
					}
				}
			}
			else
			{
				if (bank.hasCard(card))
				{
					bank.transferCard(card, hand);
				}
			}
			
			turnHistory.remove(card);
			//turns++;
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
		// TODO Auto-generated method stub
	}
	
	@Override
	public void sendMessage(String message, boolean printConsole)
	{
		// TODO Auto-generated method stub
		System.out.println(message);
	}
}
