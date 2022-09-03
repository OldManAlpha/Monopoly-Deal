package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.CardType;

public class CardActionPassGo extends CardAction
{
	@Override
	public void playCard(Player player)
	{
		Deck deck = getServer().getDeck();
		deck.drawCards(player, 2);
		getServer().getGameState().nextNaturalActionState();
	}
	
	private static CardType<CardActionPassGo> createType()
	{
		CardType<CardActionPassGo> type = new CardType<CardActionPassGo>(CardActionPassGo.class, "Pass Go", "Go");
		CardTemplate template = type.getDefaultTemplate();
		template.put("value", 1);
		template.put("name", "Pass Go");
		template.putStrings("displayName", "PASS", "GO");
		template.put("fontSize", 9);
		template.put("displayOffsetY", 2);
		template.putStrings("description", "Draw two cards.");
		template.put("revocable", false);
		template.put("clearsRevocableCards", false);
		return type;
	}
}
