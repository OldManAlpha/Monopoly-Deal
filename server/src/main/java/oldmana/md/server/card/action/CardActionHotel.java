package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;

public class CardActionHotel extends CardBuilding
{
	public CardActionHotel()
	{
		super(4, "Hotel", 1, 4);
		setDisplayName("HOTEL");
		setDisplayOffsetY(2);
		setDescription("Play onto a full property set to add 4M in rent value. Requires a house to already be built on the set. Only one hotel may be placed per "
				+ "property set. Properties under buildings are immobile. This card may be moved into your bank during your turn.");
	}
}
