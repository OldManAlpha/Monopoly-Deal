package oldmana.md.client.gui.component.collection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.util.GraphicsUtils;

public abstract class MDCardCollectionUnknown extends MDCardCollectionBase
{
	public MDCardCollectionUnknown(CardCollection collection, double scale)
	{
		super(collection, scale);
	}
	
	public List<Point> getCurrentCardPositions()
	{
		List<Point> positions = new ArrayList<Point>();
		if (getModification() != null)
		{
			List<Point> curPositions = new ArrayList<Point>();
			List<Point> prevPositions = new ArrayList<Point>();
			
			int cardCount = getCollection().getCardCount();
			for (int i = 0 ; i < cardCount ; i++)
			{
				if (getModification() == CollectionMod.ADDITION && getModIndex() == i)
				{
					continue;
				}
				curPositions.add(getLocationOf(i, cardCount));
			}
			
			int prevCardCount = getCollection().getCardCount() + (getModification() == CollectionMod.ADDITION ? -1 : 1);
			for (int i = 0 ; i < prevCardCount ; i++)
			{
				if (getModification() == CollectionMod.REMOVAL && getModIndex() == i)
				{
					continue;
				}
				prevPositions.add(getLocationOf(i, prevCardCount));
			}
			
			for (int i = 0 ; i < (getModification() == CollectionMod.REMOVAL ? cardCount : prevCardCount) ; i++)
			{
				Point p1 = curPositions.get(i);
				Point p2 = prevPositions.get(i);
				positions.add(new Point((int) (p2.x + ((p1.x - p2.x) * getVisibleShiftProgress())), 
						(int) (p2.y + ((p1.y - p2.y) * getVisibleShiftProgress()))));
			}
		}
		else
		{
			for (int i = 0 ; i < getCollection().getCardCount() ; i++)
			{
				Point p = getLocationOf(i, getCollection().getCardCount());
				positions.add(p);
			}
		}
		return positions;
	}
	
	public void paintCards(Graphics2D g)
	{
		for (Point p : getCurrentCardPositions())
		{
			g.drawImage(Card.getBackGraphics(getScale() * getCardScale()), p.x, p.y, GraphicsUtils.getCardWidth(getCardScale()), 
					GraphicsUtils.getCardHeight(getCardScale()), null);
		}
	}
}
