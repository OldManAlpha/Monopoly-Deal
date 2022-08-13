package oldmana.md.server.event;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CardMovedEvent extends Event
{
	private Card card;
	private CardCollection from;
	private CardCollection to;
	private int index;
	private double speed;
	
	public CardMovedEvent(Card card, CardCollection from, CardCollection to, int index, double speed)
	{
		this.card = card;
		this.from = from;
		this.to = to;
		this.index = index;
		this.speed = speed;
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
	
	public double getSpeed()
	{
		return speed;
	}
}
