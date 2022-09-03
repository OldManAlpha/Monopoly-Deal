package oldmana.md.client.gui.component.collection;

import java.awt.Point;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.component.MDComponent;

public abstract class MDCardCollectionBase extends MDComponent
{
	private CardCollection collection;
	private double scale;
	
	private CollectionMod mod;
	private int modIndex;
	private double moveProgress;
	
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
	
	public double getCardScale()
	{
		return scale;
	}
	
	public boolean isCardIncoming()
	{
		return mod == CollectionMod.ADDITION;
	}
	
	public boolean isCardBeingRemoved()
	{
		return mod == CollectionMod.REMOVAL;
	}
	
	public void cardArrived()
	{
		update();
	}
	
	public void startAddition(int index)
	{
		mod = CollectionMod.ADDITION;
		modIndex = index;
	}
	
	public void startRemoval(int index)
	{
		mod = CollectionMod.REMOVAL;
		modIndex = index;
	}
	
	public void modificationFinished()
	{
		mod = null;
		modIndex = -1;
		moveProgress = 0;
	}
	
	public int getModIndex()
	{
		return modIndex;
	}
	
	public void setCardMoveProgress(double progress)
	{
		moveProgress = progress;
	}
	
	public double getCardMoveProgress()
	{
		return moveProgress;
	}
	
	public double getVisibleShiftProgress()
	{
		if (mod == CollectionMod.REMOVAL)
		{
			return Math.min(1, getCardMoveProgress() * 1.5);
		}
		else
		{
			return Math.max(0, (getCardMoveProgress() * 1.5) - 0.5);
		}
	}
	
	public CollectionMod getModification()
	{
		return mod;
	}
	
	public void setModification(CollectionMod mod)
	{
		this.mod = mod;
	}
	
	public Point getLocationOf(int cardIndex)
	{
		return getLocationOf(cardIndex, getCardCount());
	}
	
	public Point getScreenLocationOf(int cardIndex)
	{
		return getScreenLocationOf(cardIndex, getCardCount());
	}
	
	public Point getScreenLocationOf(int cardIndex, int cardCount)
	{
		return SwingUtilities.convertPoint(this, getLocationOf(cardIndex, cardCount), getClient().getWindow().getTableScreen());
	}
	
	public abstract void update();
	
	public abstract Point getLocationOf(int cardIndex, int cardCount);
	
	
	public static enum CollectionMod
	{
		ADDITION, REMOVAL
	}
}
