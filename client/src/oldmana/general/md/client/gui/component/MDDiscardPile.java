package oldmana.general.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.collection.DiscardPile;
import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.gui.util.TextPainter;
import oldmana.general.md.client.gui.util.TextPainter.Alignment;

public class MDDiscardPile extends MDCardCollection
{
	public MDDiscardPile(DiscardPile discard)
	{
		super(discard, 2);
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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (getCollection() != null)
		{
			if (getCollection().getCardCount() - (isCardIncoming() ? 1 : 0) > 0)
			{
				g.setColor(Color.DARK_GRAY);
				g.fillRoundRect(scale(60), 0, scale(60) + (int) Math.ceil((getCollection().getCardCount() - (isCardIncoming() ? 1 : 0)) * (0.33 * GraphicsUtils.SCALE)), scale(180), scale(20), scale(20));
				g.drawImage(getCollection().getCardAt(getCollection().getCardCount() - 1 - (isCardIncoming() ? 1 : 0)).getGraphics(getScale() * 2), 0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), null);
			}
			else
			{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRoundRect(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), scale(20), scale(20));
				g.setColor(Color.BLACK);
				Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
				g.setFont(font);
				TextPainter tp = new TextPainter("Discard Empty", font, new Rectangle(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			
			if (getClient().isDebugEnabled())
			{
				g.setColor(Color.ORANGE);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(30), getWidth(), getHeight() / 2);
			}
		}
		//g.fillRect(60, 0, collection.getCardCount(), 90);
	}

	@Override
	public Point getLocationInComponentOf(Card card)
	{
		return new Point(0, 0);
	}
}
