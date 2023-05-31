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
					super.addCard(card, props.size());
				}
			}
			else
			{
				super.addCard(card, getPropertyCardCount());
			}
			
			if (effectiveColor == null)
			{
				List<PropertyColor> possible = getPossibleBaseColors();
				setEffectiveColor(!possible.isEmpty() ? possible.get(0) : null);
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
		super.removeCard(card);
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
		return hasBase() && isMonopoly() && effectiveColor.isBuildable();
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
		int highestTier = 0;
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
		if (props.isEmpty())
		{
			return new ArrayList<PropertyColor>();
		}
		List<PropertyColor> colors = new ArrayList<PropertyColor>(props.get(0).getColors());
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
	
	public boolean hasStealable()
	{
		for (CardProperty property : getPropertyCards())
		{
			if (property.isStealable())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isMonopoly()
	{
		return effectiveColor != null && getPropertyCardCount() >= effectiveColor.getMaxProperties();
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
	
	/**
	 * Verifies that the structure of this property set is valid and makes changes if necessary.
	 */
	public void checkLegality()
	{
		checkMaxProperties();
		checkBuildings();
	}
	
	/**
	 * Checks to see if the property set is full and if so, moves wilds cards at end of the set elsewhere. If the set
	 * has all solid colored properties, this does nothing.
	 */
	public void checkMaxProperties()
	{
		if (getPropertyCardCount() > effectiveColor.getMaxProperties())
		{
			List<CardProperty> properties = getPropertyCards();
			
			for (int i = properties.size() - 1 ; i >= 0 ; i--)
			{
				if (properties.get(i).isSingleColor() || getPropertyCardCount() <= effectiveColor.getMaxProperties())
				{
					break;
				}
				getOwner().safelyGrantProperty(properties.get(i));
			}
		}
	}
	
	/**
	 * Checks the structure of buildings on this set and moves them to the owner's bank if they're deemed invalid.
	 */
	public void checkBuildings()
	{
		if (hasBuildings())
		{
			List<CardBuilding> buildings = getBuildingCards();
			if (!isMonopoly())
			{
				for (CardBuilding building : getBuildingCards())
				{
					building.transfer(getOwner().getBank());
				}
				return;
			}
			
			for (int i = 0 ; i < buildings.size() ; i++)
			{
				CardBuilding building = buildings.get(i);
				if (building.getTier() != i + 1)
				{
					building.transfer(getOwner().getBank());
					System.out.println("Invalid building structure in set " + getID() + ": Tier " + building.getTier() + " at index " + i);
					checkBuildings();
					return;
				}
			}
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
		if (effectiveColor != color)
		{
			effectiveColor = color;
			getServer().broadcastPacket(new PacketPropertySetColor(getID(), effectiveColor != null ? effectiveColor.getID() : -1));
			getServer().getGameState().setStateChanged();
		}
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
	
	public int getPropertyCardCount()
	{
		int cardCount = 0;
		for (Card card : getCards())
		{
			if (card instanceof CardProperty)
			{
				cardCount++;
			}
		}
		return cardCount;
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
	
	public int getBuildingCardCount()
	{
		int cardCount = 0;
		for (Card card : getCards())
		{
			if (card instanceof CardBuilding)
			{
				cardCount++;
			}
		}
		return cardCount;
	}
	
	/**
	 * Moves all the cards out of this property set to a new owner, trying to maintain its structure as closely as possible.
	 * If there's solid colored cards in this set and the target player also has a solid set of the same color, this
	 * set will merge with that one.
	 * <br><br>
	 * This property set instance is considered destroyed after this method returns and should not be used again.
	 * @param player The player to give this set to
	 */
	public void transferSet(Player player)
	{
		if (player == getOwner())
		{
			return;
		}
		PropertyColor color = getEffectiveColor();
		if (hasSingleColorProperty() && player.hasSolidPropertySet(color))
		{
			PropertySet targetSet = player.getSolidPropertySet(color);
			for (CardProperty prop : getPropertyCards())
			{
				if (prop.isSingleColor() || !targetSet.isMonopoly())
				{
					prop.transfer(targetSet, -1, 0.8);
					targetSet.checkLegality();
				}
				else
				{
					player.safelyGrantProperty(prop, 0.8);
				}
			}
			for (CardBuilding building : getBuildingCards())
			{
				building.transfer(targetSet, -1, 0.8);
			}
			if (targetSet.getEffectiveColor() != color)
			{
				targetSet.setEffectiveColor(color);
			}
		}
		else
		{
			PropertySet targetSet = player.createPropertySet();
			for (Card card : getCards(true))
			{
				card.transfer(targetSet, -1, 0.8);
			}
			if (targetSet.getEffectiveColor() != color)
			{
				targetSet.setEffectiveColor(color);
			}
		}
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
