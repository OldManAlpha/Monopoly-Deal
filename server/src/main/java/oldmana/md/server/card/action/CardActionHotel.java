package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionHotel extends CardBuilding
{
	private static CardType<CardActionHotel> createType()
	{
		CardType<CardActionHotel> type = new CardType<CardActionHotel>(CardActionHotel.class,
				CardActionHotel::new, "Hotel");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 4);
		template.put(NAME, "Hotel");
		template.putStrings(DISPLAY_NAME, "HOTEL");
		template.put(FONT_SIZE, 8);
		template.put(DISPLAY_OFFSET_Y, 2);
		template.putStrings(DESCRIPTION, "Play onto a full property set to add 4M in rent value. Requires a house " +
				"to already be built on the set. Only one hotel may be placed per property set. Properties under " +
				"buildings are immobile. This card may be moved into your bank during your turn.");
		template.put(CardBuilding.TIER, 2);
		template.put(CardBuilding.RENT_ADDITION, 4);
		type.setDefaultTemplate(template);
		return type;
	}
}
