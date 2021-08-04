package oldmana.md.client.card.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.gui.component.collection.MDPropertySet;

public class PropertySet extends CardCollection
{
	private PropertyColor effectiveColor;
	
	public PropertySet(int id, Player owner)
	{
		super(id, owner);
		setUI(new MDPropertySet(this));
	}
	
	public PropertySet(int id, Player owner, CardProperty card, PropertyColor effectiveColor)
	{
		super(id, owner);
		this.effectiveColor = effectiveColor;
		setUI(new MDPropertySet(this));
		addCard(card);
	}
	
	public PropertySet(int id, Player owner, List<Card> cards, PropertyColor effectiveColor)
	{
		super(id, owner);
		this.effectiveColor = effectiveColor;
		setUI(new MDPropertySet(this));
		for (Card card : cards)
		{
			addCard(card);
		}
	}
	
	@Override
	public void addCard(Card card)
	{
		if (card instanceof CardProperty)
		{
			CardProperty property = (CardProperty) card;
			addCard(property);
		}
		else
		{
			super.addCard(card);
		}
	}
	
	public void addCard(CardProperty card)
	{
		super.addCard(card);
		if (getCardCount() == 1)
		{
			if (!card.isPropertyWildCard() && (effectiveColor == null || !card.hasColor(effectiveColor)))
			{
				effectiveColor = card.getColor();
			}
		}
	}
	
	public void removeCard(CardProperty card)
	{
		getCards().remove(card);
	}
	
	public boolean hasCard(CardProperty card)
	{
		return hasCard((Card) card);
	}
	
	public CardProperty getPropertyCardAt(int index)
	{
		return (CardProperty) getCards().get(index);
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
	
	public List<PropertyColor> getPossibleMonopolyColors()
	{
		List<PropertyColor> colors = getPossibleBaseColors();
		Iterator<PropertyColor> it = colors.iterator();
		while (it.hasNext())
		{
			PropertyColor color = it.next();
			if (!color.isBuildable() || color.getMaxProperties() != getPropertyCount())
			{
				it.remove();
			}
		}
		return colors;
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
	
	public int getPropertyCount()
	{
		return getPropertyCards().size();
	}
}
