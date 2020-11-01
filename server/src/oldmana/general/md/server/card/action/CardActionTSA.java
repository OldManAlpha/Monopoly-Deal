package oldmana.general.md.server.card.action;

import java.util.Random;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.card.collection.Hand;

public class CardActionTSA extends CardAction
{
	public CardActionTSA()
	{
		super(2, "TSA");
		setDisplayName("TSA");
		setFontSize(10);
		setDisplayOffsetY(1);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		Random r = new Random();
		for (Player p : getServer().getPlayersExcluding(player))
		{
			Hand hand = p.getHand();
			if (hand.getCardCount() > 0)
			{
				Card card = hand.getCardAt(r.nextInt(hand.getCardCount()));
				card.transfer(getServer().getDiscardPile(), -1, 0.6);
				card.transfer(hand, -1, 0.6);
			}
		}
	}
}
