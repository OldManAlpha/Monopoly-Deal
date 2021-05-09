package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.collection.Deck;

public class CardActionPassGo extends CardAction
{
	public CardActionPassGo()
	{
		super(1, "Pass Go");
		setDisplayName("PASS", "GO");
		setFontSize(9);
		setDisplayOffsetY(2);
		setRevocable(false);
		setDescription("Draw two cards.");
	}
	
	@Override
	public void playCard(Player player)
	{
		Deck deck = getServer().getDeck();
		deck.drawCards(player, 2);
		getServer().getGameState().nextNaturalActionState();
	}
}
