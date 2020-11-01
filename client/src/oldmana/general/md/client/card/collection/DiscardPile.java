package oldmana.general.md.client.card.collection;

import java.util.List;

import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.gui.component.MDDiscardPile;

public class DiscardPile extends CardCollection
{
	public DiscardPile(int id, List<Card> cards)
	{
		super(id, null);
		for (Card card : cards)
		{
			addCard(card);
		}
		//setUI(new MDDiscardPile(this));
	}
}
