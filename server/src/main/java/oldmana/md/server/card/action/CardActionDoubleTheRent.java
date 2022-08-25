package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.type.CardType;

public class CardActionDoubleTheRent extends CardAction
{
	@Override
	public boolean canPlayCard(Player player)
	{
		return false;
	}
	
	@Override
	public CardTypeLegacy getTypeLegacy()
	{
		return CardTypeLegacy.DOUBLE_THE_RENT;
	}
	
	private static CardType<CardActionDoubleTheRent> createType()
	{
		CardType<CardActionDoubleTheRent> type = new CardType<CardActionDoubleTheRent>(CardActionDoubleTheRent.class, "Double The Rent!",
				"Double Rent");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 1);
		template.put("name", "Double The Rent!");
		template.putStrings("displayName", "DOUBLE", "THE RENT!");
		template.put("fontSize", 7);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Can be played with a Rent card to double the charge against players. Counts as a turn.");
		template.put("revocable", false);
		template.put("clearsRevocableCards", true);
		return type;
	}
}
