package oldmana.md.server.card.property;

import oldmana.md.server.card.CardProperty;

public class CardPropertyRainbowWildCard extends CardProperty
{
	public CardPropertyRainbowWildCard()
	{
		super(PropertyColor.values(), 0, "Property Wild Card", false);
		setDescription("A property card that can be paired with any color. It cannot be used for rent if you don't have another property with the color. "
				+ "This card cannot be stolen with Sly Deals, Forced Deals, or rent.");
	}
}
