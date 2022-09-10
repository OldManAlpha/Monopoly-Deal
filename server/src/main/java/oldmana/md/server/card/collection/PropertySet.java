package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketMovePropertySet;
import oldmana.md.net.packet.server.PacketPropertySetColor;
import oldmana.md.net.packet.server.PacketPropertySetData;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.PropertyColor;

public class PropertySet extends CardCollection
{
	private PropertyColor effectiveColor;
	
	public PropertySet(Player owner)
	{
		super(owner);
		getServer().broadcastPacket(getCollectionDataPacket());
	}
	
	public PropertySet(Player owner, CardProperty card)
	{
		super(owner);
		if (card.isBase())
		{
			effectiveColor = card.getColor();
		}
		addCard(card);
		getServer().broadcastPacket(getCollectionDataPacket());
	}
	
	@Override
	public void addCard(Card card)
	{
		if (card instanceof CardProperty)
		{
			addCard((CardProperty) card);
		}
		else if (card instanceof CardBuilding)
		{
			addCard((CardBuilding) card);
		}
	}
	
	@Override
	public void addCard(Card card, int index)
	{
		if (card instanceof CardProperty)
		{
			addCard((CardProperty) card);
		}
		else if (card instanceof CardBuilding)
		{
			addCard((CardBuilding) card);
		}
	}
	
	@Override
	public void removeCard(Card card)
	{
		if (card instanceof CardProperty)
		{
			removeCard((CardProperty) card);
		}
		else
		{
			super.removeCard(card);
		}
	}
	
	public void addCard(CardProperty card)
	{
		if (effectiveColor == null || card.hasColor(effectiveColor) || isEmpty())
		{
			if (getCardCount() == 0)
			{
				super.addCard(card);
				if (card.isBase())
				{
					setEffectiveColor(card.getColor());
				}
			}
			else if (card.isSingleColor())
			{
				List<CardProperty> props = getPropertyCards();
				boolean cardAdded = false;
				for (int i = 0 ; i < props.size() ; i++)
				{
					if (!props.get(i).isSingleColor())
					{
						super.addCard(card, i);
						cardAdded = true;
						break;
					}
				}
				if (!cardAdded)
				{
					super.addCard(card);
				}
			}
			else
			{
				super.addCard(card);
			}
			
			if (effectiveColor == null && card.isBase())
			{
				setEffectiveColor(card.getColor());
			}
		}
		else if (isCompatibleWith(card))
		{
			super.addCard(card);
			setEffectiveColor(getPossibleBaseColors().get(0));
		}
	}
	
	public void removeCard(CardProperty card)
	{
		getCards().remove(card);
		if (getCardCount() > 0)
		{
			if (!hasBase())
			{
				setEffectiveColor(null);
			}
			else if (!getPossibleColors().contains(effectiveColor))
			{
				setEffectiveColor(((CardProperty) getCardAt(0)).getColor());
			}
		}
	}
	
	public boolean hasCard(CardProperty card)
	{
		return hasCard((Card) card);
	}
	
	public void addCard(CardBuilding building)
	{
		super.addCard(building);
	}
	
	public boolean canBuild()
	{
		return hasBase() && isMonopoly();
	}
	
	public boolean hasBuildings()
	{
		for (Card card : getCards())
		{
			if (card instanceof CardBuilding)
			{
				return true;
			}
		}
		return false;
	}
	
	public int getHighestBuildingTier()
	{
		int highestTier = -1;
		for (CardBuilding card : getBuildingCards())
		{
			highestTier = Math.max(highestTier, card.getTier());
		}
		return highestTier;
	}
	
	public int getBuildingRentAddition()
	{
		int rentAddition = 0;
		for (CardBuilding building : getBuildingCards())
		{
			rentAddition += building.getRentAddition();
		}
		return rentAddition;
	}
	
	public List<PropertyColor> getPossibleColors()
	{
		List<CardProperty> props = getPropertyCards();
		List<PropertyColor> colors = new ArrayList<PropertyColor>(((CardProperty) props.get(0)).getColors());
		if (props.size() > 1)
		{
			for (int i = 1 ; i < props.size() ; i++)
			{
				colors.retainAll(props.get(i).getColors());
			}
		}
		return colors;
	}
	
	public List<PropertyColor> getPossibleBaseColors()
	{
		List<PropertyColor> colors = null;
		boolean hasBase = false;
		List<CardProperty> props = getPropertyCards();
		for (int i = 0 ; i < props.size() ; i++)
		{
			CardProperty prop = props.get(i);
			
			if (prop.isBase())
			{
				hasBase = true;
			}
			
			if (colors == null)
			{
				colors = new ArrayList<PropertyColor>(prop.getColors());
			}
			else
			{
				colors.retainAll(prop.getColors());
			}
		}
		return colors == null || !hasBase ? new ArrayList<PropertyColor>() : colors;
	}
	
	public boolean hasBase()
	{
		for (CardProperty property : getPropertyCards())
		{
			if (property.isBase())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isMonopoly()
	{
		return effectiveColor != null && getCardCount() >= effectiveColor.getMaxProperties();
	}
	
	public boolean hasSingleColorProperty()
	{
		for (CardProperty property : getPropertyCards())
		{
			if (property.isSingleColor())
			{
				return true;
			}
		}
		return false;
	}
	
	public void checkMaxProperties()
	{
		List<CardProperty> cards = getPropertyCards();
		if (cards.size() > effectiveColor.getMaxProperties())
		{
			PropertySet newSet = getOwner().createPropertySet();
			Card card = cards.get(cards.size() - 1);
			card.transfer(newSet);
		}
	}
	
	public boolean isCompatibleWith(CardProperty property)
	{
		for (PropertyColor color : getPossibleColors())
		{
			if (property.getColors().contains(color))
			{
				return true;
			}
		}
		return false;
	}
	
	public PropertyColor getEffectiveColor()
	{
		return effectiveColor;
	}
	
	public void setEffectiveColor(PropertyColor color)
	{
		effectiveColor = color;
		getServer().broadcastPacket(new PacketPropertySetColor(getID(), effectiveColor != null ? effectiveColor.getID() : -1));
	}
	
	public List<CardProperty> getPropertyCards()
	{
		List<CardProperty> properties = new ArrayList<CardProperty>();
		for (Card card : getCards())
		{
			if (card instanceof CardProperty)
			{
				properties.add((CardProperty) card);
			}
		}
		return properties;
	}
	
	public List<CardBuilding> getBuildingCards()
	{
		List<CardBuilding> buildings = new ArrayList<CardBuilding>();
		for (Card card : getCards())
		{
			if (card instanceof CardBuilding)
			{
				buildings.add((CardBuilding) card);
			}
		}
		return buildings;
	}
	
	@Override
	public void transferCard(Card card, CardCollection to, int index, double time, CardAnimationType anim, boolean flash)
	{
		super.transferCard(card, to, index, time, anim, flash);
		if (getCardCount() == 0)
		{
			getOwner().destroyPropertySet(this);
		}
	}
	
	@Override
	public void setOwner(Player player)
	{
		super.setOwner(player);
		getServer().broadcastPacket(new PacketMovePropertySet(getID(), player.getID(), -1));
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return true;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketPropertySetData(getID(), getOwner().getID(), getCardIDs(), getEffectiveColor() != null ? getEffectiveColor().getID() : -1);
	}
}
