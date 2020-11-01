package oldmana.md.client.gui.component;

import java.awt.Point;

import javax.swing.JLayeredPane;

import oldmana.md.client.MDClient;
import oldmana.md.client.card.collection.CardCollection;

public abstract class MDCardCollectionBase extends MDComponent
{
	private CardCollection collection;
	private double scale;
	private boolean cardIncoming;
	
	public MDCardCollectionBase(CardCollection collection, double scale)
	{
		super();
		this.collection = collection;
		this.scale = scale;
	}
	
	public CardCollection getCollection()
	{
		return collection;
	}
	
	public void setCollection(CardCollection collection)
	{
		this.collection = collection;
	}
	
	public int getCardCount()
	{
		return collection.getCardCount();
	}
	
	public double getComponentScale()
	{
		return scale;
	}
	
	public boolean isCardIncoming()
	{
		return cardIncoming;
	}
	
	public void cardIncoming()
	{
		cardIncoming = true;
	}
	
	public void cardArrived()
	{
		cardIncoming = false;
		update();
	}
	
	public abstract void update();
	
	public abstract Point getLocationOf(int cardIndex);
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
