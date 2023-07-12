package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

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
		updateGraphics();
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		Color textColor = new Color(80, 80, 80);
		Color outlineColor = new Color(220, 220, 220);
		if (getModification() == CollectionMod.ADDITION && getCardCount() == 1)
		{
			double prog = getVisibleShiftProgress();
			textColor = new Color(80, 80, 80, (int) (255 - (255 * prog)));
			outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 255 - (int) (prog * 255));
		}
		else if (getModification() == CollectionMod.REMOVAL && getCardCount() == 0)
		{
			double prog = getVisibleShiftProgress();
			textColor = new Color(80, 80, 80, (int) (255 * prog));
			outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), (int) (prog * 255));
		}
		if ((getCardCount() == 1 && getModification() == CollectionMod.ADDITION) || getCardCount() == 0)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(outlineColor);
			g.fillRoundRect(scale(0), scale(0), getWidth(), getHeight(), scale(15), scale(15));
			
			g.setColor(textColor);
			TextPainter tp = new TextPainter("Hand Empty", GraphicsUtils.getBoldMDFont(scale(20)), new Rectangle(0, 0, getWidth(), getHeight()));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
		
		paintCards(g);
		
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
