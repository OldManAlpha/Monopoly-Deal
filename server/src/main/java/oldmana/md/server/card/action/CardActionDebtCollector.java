package oldmana.md.server.card.action;

import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

public class CardActionDebtCollector extends CardActionCharge
{
	private static CardType<CardActionDebtCollector> createType()
	{
		CardType<CardActionDebtCollector> type = new CardType<CardActionDebtCollector>(CardActionDebtCollector.class,
				CardActionDebtCollector::new, "Debt Collector");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 3);
		template.put("name", "Debt Collector");
		template.putStrings("displayName", "DEBT", "COLLECTOR");
		template.put("fontSize", 6);
		template.put("displayOffsetY", 1);
		template.putStrings("description", "Select a player and charge 5M against them.");
		template.put("chargesAll", false);
		template.put("charge", 5);
		type.setDefaultTemplate(template);
		return type;
	}
}
