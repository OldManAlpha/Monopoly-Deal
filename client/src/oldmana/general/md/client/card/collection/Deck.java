package oldmana.general.md.client.card.collection;

import oldmana.general.md.client.gui.component.MDDeck;

public class Deck extends CardCollection
{
	public Deck(int id, int cardCount)
	{
		super(id, null, cardCount);
		//setUI(new MDDeck(this));
	}
}
