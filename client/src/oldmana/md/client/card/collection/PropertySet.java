package oldmana.md.client.card.collection;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.gui.component.MDPropertySet;

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
	
	public PropertySet(int id, Player owner, List<CardProperty> cards, PropertyColor effectiveColor)
	{
		super(id, owner);
		this.effectiveColor = effectiveColor;
		setUI(new MDPropertySet(this));
		for (CardProperty card : cards)
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
	
	public List<CardProperty> getPropertyCards()
	{
		List<CardProperty> properties = new ArrayList<CardProperty>();
		for (Card card : getCards())
		{
			properties.add((CardProperty) card);
		}
		return properties;
	}
}
