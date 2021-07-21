package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDInvisibleHand extends MDCardCollectionUnknown
{
	public MDInvisibleHand(Hand hand)
	{
		super(hand, 1);
		update();
	}
	
	@Override
	public void update()
	{
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		CardCollection hand = getCollection();
		if (hand.isEmpty())
		{
			g.setColor(Color.DARK_GRAY);
			Font font = GraphicsUtils.getBoldMDFont(scale(20));
			g.setFont(font);
			TextPainter tp = new TextPainter("Hand Empty", font, new Rectangle(0, 0, getWidth(), getHeight()));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
		else
		{
			paintCards(g);
		}
		
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.MAGENTA);
			GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(20), getWidth(), getHeight());
		}
	}
	
	public double getInterval(int cardCount)
	{
		return cardCount == 1 ? 0 : Math.min(scale(30), (getWidth() - GraphicsUtils.getCardWidth()) / (double) (cardCount - 1));
	}
	
	public double getInterval()
	{
		return getInterval(getCollection().getCardCount());
	}
	
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point((int) (getInterval(cardCount) * cardIndex), 0);
	}
}
