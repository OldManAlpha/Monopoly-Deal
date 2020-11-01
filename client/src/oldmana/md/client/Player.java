package oldmana.md.client;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardActionDoubleTheRent;
import oldmana.md.client.card.CardActionJustSayNo;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDBank;
import oldmana.md.client.gui.component.MDInvisibleHand;
import oldmana.md.client.gui.component.large.MDHand;
import oldmana.md.client.gui.component.large.MDPlayer;

public class Player
{
	private MDClient client;
	
	private int id;
	
	private String name;
	
	private boolean connected = true;
	
	private Hand hand;
	private Bank bank;
	private List<PropertySet> propertySets;
	
	//private List<Card> turnHistory = new ArrayList<Card>();
	
	private MDPlayer ui;
	private int uiPos;
	
	public Player(MDClient client, int id, String name)
	{
		this.client = client;
		
		this.id = id;
		this.name = name;
		
		propertySets = new ArrayList<PropertySet>();
		
		ui = new MDPlayer(this);
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void setHand(Hand hand)
	{
		this.hand = hand;
		if (this instanceof ThePlayer)
		{
			client.getTableScreen().setHand(hand);
		}
		else
		{
			ui.setHand((MDInvisibleHand) hand.getUI());
		}
	}
	
	public Hand getHand()
	{
		return hand;
	}
	
	public void setBank(Bank bank)
	{
		this.bank = bank;
		ui.setBank((MDBank) bank.getUI());
	}
	
	public Bank getBank()
	{
		return bank;
	}
	
	public boolean hasJustSayNo()
	{
		if (this instanceof ThePlayer)
		{
			for (Card card : getHand().getCards())
			{
				if (card instanceof CardActionJustSayNo)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasDoubleTheRent()
	{
		if (this instanceof ThePlayer)
		{
			for (Card card : getHand().getCards())
			{
				if (card instanceof CardActionDoubleTheRent)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public CardActionDoubleTheRent getFirstDoubleTheRent()
	{
		if (this instanceof ThePlayer)
		{
			for (Card card : getHand().getCards())
			{
				if (card instanceof CardActionDoubleTheRent)
				{
					return (CardActionDoubleTheRent) card;
				}
			}
		}
		return null;
	}
	
	public boolean hasAllPropertiesInHand()
	{
		if (this instanceof ThePlayer)
		{
			for (Card card : getHand().getCards())
			{
				if (!(card instanceof CardProperty))
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
		return true;
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
	
	public boolean hasCompatiblePropertySet(CardProperty property)
	{
		for (PropertySet set : getPropertySets())
		{
			if (set.isCompatibleWith(property))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean hasCompatiblePropertySetWithRoom(CardProperty property)
	{
		for (PropertySet set : getPropertySets())
		{
			if (!set.isMonopoly() && set.isCompatibleWith(property))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSingleColorPropertySet(PropertyColor color)
	{
		return getSingleColorPropertySet(color) != null;
	}
	
	public PropertySet getSingleColorPropertySet(PropertyColor color)
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
	
	public void destroyPropertySet(PropertySet set)
	{
		propertySets.remove(set);
		ui.removePropertySet(set);
	}
	
	public void addPropertySet(PropertySet set)
	{
		propertySets.add(set);
		ui.addPropertySet(set);
	}
	
	public void transferPropertySet(PropertySet set, Player player)
	{
		propertySets.remove(set);
		player.addPropertySet(set);
		set.setOwner(this);
	}
	
	public MDPlayer getUI()
	{
		return ui;
	}
	
	public int getUIPosition()
	{
		return uiPos;
	}
	
	public void setUIPosition(int pos)
	{
		uiPos = pos;
	}
}
