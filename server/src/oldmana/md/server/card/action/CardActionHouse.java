package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;

public class CardActionHouse extends CardBuilding
{
	public CardActionHouse()
	{
		super(3, "House", 0, 3);
		setDisplayName("HOUSE");
		setDescription("Play onto a full property set to add 3M in rent value. Only one house may be placed per property set. Properties under buildings are "
				+ "immobile. This card may be moved into your bank during your turn.");
	}
}
