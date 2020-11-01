package oldmana.md.server.card.action;

import java.util.Random;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.card.collection.Deck;

public class CardActionThisHandIsShit extends CardAction
{
	public CardActionThisHandIsShit()
	{
		super(1, "This Hand Is Shit");
		setDisplayName("THIS HAND", "IS SHIT");
		setFontSize(6);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		Random r = new Random();
		Deck deck = getServer().getDeck();
		int handCount = player.getHand().getCardCount();
		for (Card card : player.getHand().getCards(true))
		{
			card.transfer(deck, r.nextInt(deck.getCardCount()), 2);
		}
		deck.drawCards(player, Math.min(handCount, 7), 1.2);
	}
}
