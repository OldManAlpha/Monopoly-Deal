package oldmana.md.server.card.action;

import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import static oldmana.md.server.card.CardAttributes.*;

public class CardActionDebtCollector extends CardActionCharge
{
	private static CardType<CardActionDebtCollector> createType()
	{
		CardType<CardActionDebtCollector> type = new CardType<CardActionDebtCollector>(CardActionDebtCollector.class,
				CardActionDebtCollector::new, "Debt Collector");
		CardTemplate template = type.getDefaultTemplate();
		template.put(VALUE, 3);
		template.put(NAME, "Debt Collector");
		template.putStrings(DISPLAY_NAME, "DEBT", "COLLECTOR");
		template.put(FONT_SIZE, 6);
		template.put(DISPLAY_OFFSET_Y, 1);
		template.putStrings(DESCRIPTION, "Select a player and charge 5M against them.");
		template.put(CardActionCharge.CHARGES_ALL, false);
		template.put(CardActionCharge.CHARGE, 5);
		type.setDefaultTemplate(template);
		return type;
	}
}
