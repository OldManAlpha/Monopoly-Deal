package oldmana.md.client.card.collection;

import java.util.List;

import oldmana.md.client.card.Card;

public class DiscardPile extends CardCollection
{
	public DiscardPile(int id, List<Card> cards)
	{
		super(id, null);
		for (Card card : cards)
		{
			addCard(card);
		}
	}
}
