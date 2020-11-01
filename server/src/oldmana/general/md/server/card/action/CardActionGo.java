package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.card.collection.Deck;

public class CardActionGo extends CardAction
{
	public CardActionGo()
	{
		super(1, "Go");
		setDisplayName("PASS", "GO");
		setFontSize(9);
		setDisplayOffsetY(2);
		setRevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		Deck deck = getServer().getDeck();
		deck.drawCards(player, 2);
		getServer().getGameState().nextNaturalActionState();
		/*
		for (int i = 0 ; i < 2 ; i++)
		{
			deck.drawCard(getOwner());
		}
		*/
		//getServer().getGameState().nextNaturalActionState();
	}
}
