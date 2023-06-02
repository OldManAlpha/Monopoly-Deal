package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

public class CardActionHotel extends CardBuilding
{
	private static CardType<CardActionHotel> createType()
	{
		CardType<CardActionHotel> type = new CardType<CardActionHotel>(CardActionHotel.class,
				CardActionHotel::new, "Hotel");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 4);
		template.put("name", "Hotel");
		template.putStrings("displayName", "HOTEL");
		template.put("fontSize", 8);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Play onto a full property set to add 4M in rent value. Requires a house " +
				"to already be built on the set. Only one hotel may be placed per property set. Properties under " +
				"buildings are immobile. This card may be moved into your bank during your turn.");
		template.put("tier", 2);
		template.put("rentAddition", 4);
		type.setDefaultTemplate(template);
		return type;
	}
}
