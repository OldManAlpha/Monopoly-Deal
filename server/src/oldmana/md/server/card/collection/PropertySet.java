package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketPropertySetColor;
import oldmana.md.net.packet.server.PacketPropertySetData;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;

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
		if (!card.isPropertyWildCard())
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
			CardProperty property = (CardProperty) card;
			addCard(property);
		}
	}
	
	@Override
	public void addCard(Card card, int index)
	{
		addCard((CardProperty) card);
	}
	
	@Override
	public void removeCard(Card card)
	{
		removeCard((CardProperty) card);
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
	
	public List<PropertyColor> getPossibleColors()
	{
		List<PropertyColor> colors = new ArrayList<PropertyColor>(((CardProperty) getCards().get(0)).getColors());
		if (getCardCount() > 1)
		{
			for (int i = 1 ; i < getCardCount() ; i++)
			{
				colors.retainAll(((CardProperty) getCards().get(i)).getColors());
			}
		}
		return colors;
	}
	
	public List<PropertyColor> getPossibleBaseColors()
	{
		List<PropertyColor> colors = null;
		for (int i = 0 ; i < getCardCount() ; i++)
		{
			CardProperty prop = (CardProperty) getCards().get(i);
			if (colors == null && prop.isBase())
			{
				colors = new ArrayList<PropertyColor>(prop.getColors());
			}
			else if (colors != null)
			{
				colors.retainAll(prop.getColors());
			}
		}
		return colors == null ? new ArrayList<PropertyColor>() : colors;
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
		if (getCardCount() > effectiveColor.getMaxProperties())
		{
			PropertySet newSet = getOwner().createPropertySet();
			Card card = getCardAt(getCardCount() - 1);
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
			properties.add((CardProperty) card);
		}
		return properties;
	}
	
	@Override
	public void transferCard(Card card, CardCollection to, int index, double speed)
	{
		super.transferCard(card, to, index, speed);
		if (getCardCount() == 0)
		{
			getOwner().destroyPropertySet(this);
		}
	}
	
	@Override
	public void transferCard(Card card, CardCollection to, int index)
	{
		super.transferCard(card, to, index);
		if (getCardCount() == 0)
		{
			getOwner().destroyPropertySet(this);
		}
	}
	
	@Override
	public void transferCard(Card card, CardCollection to)
	{
		super.transferCard(card, to);
		if (getCardCount() == 0)
		{
			getOwner().destroyPropertySet(this);
		}
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return true;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketPropertySetData(getID(), getOwner().getID(), getCardIds(), getEffectiveColor() != null ? getEffectiveColor().getID() : -1);
	}
}
