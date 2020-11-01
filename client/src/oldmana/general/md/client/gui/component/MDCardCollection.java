package oldmana.general.md.client.gui.component;

import java.awt.Point;

import javax.swing.SwingUtilities;

import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.collection.CardCollection;

public abstract class MDCardCollection extends MDCardCollectionBase
{
	private Card incomingCard;
	
	public MDCardCollection(CardCollection collection, double scale)
	{
		super(collection, scale);
		update();
	}
	
	public MDCardCollection(CardCollection collection)
	{
		this(collection, 1);
	}
	
	public Card getIncomingCard()
	{
		return incomingCard;
	}
	
	public void setIncomingCard(Card card)
	{
		cardIncoming();
		incomingCard = card;
		update();
	}
	
	@Override
	public void cardArrived()
	{
		super.cardArrived();
		incomingCard = null;
		update();
	}
	
	@Override
	public Point getLocationOf(int cardIndex)
	{
		return getLocationOf(getCollection().getCardAt(cardIndex));
	}
	
	public Point getLocationOf(Card card)
	{
		return SwingUtilities.convertPoint(this, getLocationInComponentOf(card), getClient().getWindow().getTableScreen());
	}
	
	public abstract Point getLocationInComponentOf(Card card);
}
