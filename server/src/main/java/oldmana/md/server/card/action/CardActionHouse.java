package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionHouse extends CardBuilding
{
	private static CardType<CardActionHouse> createType()
	{
		CardType<CardActionHouse> type = new CardType<CardActionHouse>(CardActionHouse.class,
				CardActionHouse::new, "House");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 3);
		template.put(NAME, "House");
		template.putStrings(DISPLAY_NAME, "HOUSE");
		template.put(FONT_SIZE, 8);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Place onto a full property set to add 3M in rent value. Only one House may " +
				"be placed per property set. Cannot be placed on railroads and utilities. Properties under buildings " +
				"are immobile. This card may be moved into your bank during your turn.");
		template.put(CardBuilding.TIER, 1);
		template.put(CardBuilding.RENT_ADDITION, 3);
		type.setDefaultTemplate(template);
		return type;
	}
}
