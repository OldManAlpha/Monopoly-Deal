package oldmana.md.server.event.card;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.event.Event;

public class CardMovedEvent extends Event
{
	private Card card;
	private CardCollection from;
	private CardCollection to;
	private int index;
	private double time;
	
	public CardMovedEvent(Card card, CardCollection from, CardCollection to, int index, double time)
	{
		this.card = card;
		this.from = from;
		this.to = to;
		this.index = index;
		this.time = time;
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public CardCollection getFrom()
	{
		return from;
	}
	
	public CardCollection getTo()
	{
		return to;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public double getTime()
	{
		return time;
	}
}
