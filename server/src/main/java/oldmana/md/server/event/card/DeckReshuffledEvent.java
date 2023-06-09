package oldmana.md.server.event.card;

import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.event.Event;

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
