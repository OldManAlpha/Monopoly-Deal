package oldmana.md.client;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.collection.MDBank;
import oldmana.md.client.gui.component.collection.MDInvisibleHand;
import oldmana.md.client.gui.component.large.MDPlayer;

public class Player
{
	protected MDClient client;
	
	private int id;
	
	private String name;
	
	private boolean connected = true;
	
	protected Hand hand;
	protected Bank bank;
	protected List<PropertySet> propertySets;
	
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
	
	public void setName(String name)
	{
		this.name = name;
		getUI().updateGraphics();
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
		getUI().updateGraphics();
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void setHand(Hand hand)
	{
		this.hand = hand;
		ui.setHand((MDInvisibleHand) hand.getUI());
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
	
	public boolean hasAllPropertiesInHand()
	{
		throw new UnsupportedOperationException("Only applicable to the client player");
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
	
	public List<PropertySet> getPropertySetsCompatibleWithBuildingTier(int tier)
	{
		List<PropertySet> sets = new ArrayList<PropertySet>();
		for (PropertySet set : propertySets)
		{
			if (set.isMonopoly() && set.getEffectiveColor().isBuildable() && tier == set.getHighestBuildingTier() + 1)
			{
				sets.add(set);
			}
		}
		return sets;
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
}
