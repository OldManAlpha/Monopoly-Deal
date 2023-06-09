package oldmana.md.server.card.action;

import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionItsMyBirthday extends CardActionCharge
{
	private static CardType<CardActionItsMyBirthday> createType()
	{
		CardType<CardActionItsMyBirthday> type = new CardType<CardActionItsMyBirthday>(CardActionItsMyBirthday.class,
				CardActionItsMyBirthday::new, "It's My Birthday",
				"Birthday");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 2);
		template.put(NAME, "It's My Birthday");
		template.putStrings(DISPLAY_NAME, "IT'S MY", "BIRTHDAY");
		template.put(FONT_SIZE, 7);
		template.put(DISPLAY_OFFSET_Y, 0);
		template.putStrings(DESCRIPTION, "Charge all other players 2M.");
		template.put(CHARGES_ALL, true);
		template.put(CHARGE, 2);
		type.setDefaultTemplate(template);
		return type;
	}
}
