package oldmana.md.server.card.play.argument;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.play.PlayArgument;

public class CardArgument implements PlayArgument
{
	private Card card;
	
	public CardArgument(Card card)
	{
		this.card = card;
	}
	
	public Card getCard()
	{
		return card;
	}
}
