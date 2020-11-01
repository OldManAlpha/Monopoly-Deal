package oldmana.general.md.server.history;

import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.collection.CardCollection;

public class CardTransfer
{
	private Card card;
	private CardCollection from;
	private int fromIndex;
	private CardCollection to;
	private int toIndex;
	
	public CardTransfer(Card card, CardCollection from, CardCollection to)
	{
		this.card = card;
		this.from = from;
		this.to = to;
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
	
	public void undoTransfer()
	{
		
	}
}
