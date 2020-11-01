package oldmana.md.client.gui.component;

import java.awt.Point;

import oldmana.md.client.card.collection.CardCollection;

public class MDVoidCollection extends MDCardCollectionUnknown
{
	public MDVoidCollection(CardCollection collection)
	{
		super(collection, 1);
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public Point getLocationOf(int cardIndex)
	{
		return new Point(0, 0);
	}
}
