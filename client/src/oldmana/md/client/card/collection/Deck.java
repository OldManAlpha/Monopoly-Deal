package oldmana.md.client.card.collection;

import oldmana.md.client.gui.component.MDDeck;

public class Deck extends CardCollection
{
	public Deck(int id, int cardCount)
	{
		super(id, null, cardCount);
		//setUI(new MDDeck(this));
	}
}
