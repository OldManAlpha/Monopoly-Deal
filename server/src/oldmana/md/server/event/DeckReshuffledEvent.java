package oldmana.md.server.event;

import oldmana.md.server.card.collection.Deck;

public class DeckReshuffledEvent extends Event
{
	private Deck deck;
	
	public DeckReshuffledEvent(Deck deck)
	{
		this.deck = deck;
	}
	
	public Deck getDeck()
	{
		return deck;
	}
}
