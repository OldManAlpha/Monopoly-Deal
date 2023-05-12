package oldmana.md.server.card.action;

import oldmana.md.server.card.CardBuilding;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

public class CardActionHouse extends CardBuilding
{
	private static CardType<CardActionHouse> createType()
	{
		CardType<CardActionHouse> type = new CardType<CardActionHouse>(CardActionHouse.class,
				CardActionHouse::new, "House");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "House");
		template.putStrings("displayName", "HOUSE");
		template.put("fontSize", 7);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Place onto a full property set to add 3M in rent value. Only one House may " +
				"be placed per property set. Cannot be placed on railroads and utilties. Properties under buildings " +
				"are immobile. This card may be moved into your bank during your turn.");
		template.put("tier", 1);
		template.put("rentAddition", 3);
		type.setDefaultTemplate(template);
		return type;
	}
}
