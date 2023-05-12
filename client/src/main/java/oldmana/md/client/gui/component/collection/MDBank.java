package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDBank extends MDCardCollection
{
	private Card hovered;
	
	public MDBank(Bank bank)
	{
		super(bank);
		
		MouseAdapter listener = new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				setHovered(null);
			}
			
			@Override
			public void mouseMoved(MouseEvent e)
			{
				if (e.getY() > scale(25))
				{
					for (int i = getCardCount() - 1 ; i >= 0 ; i--)
					{
						Point loc = getLocationOf(i, getCardCount());
						if (loc.getX() < e.getX() && loc.getX() + GraphicsUtils.getCardWidth() > e.getX())
						{
							setHovered(getCollection().getCardAt(i));
							return;
						}
					}
				}
				setHovered(null);
			}
		};
		
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
	
	public void setHovered(Card card)
	{
		if (hovered != card)
		{
			hovered = card;
			repaint();
		}
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
		Graphics2D debug = (Graphics2D) g.create();
		g.setColor(Color.DARK_GRAY);
		Color gray = new Color(150, 150, 150);
		Color textColor = Color.BLACK;
		Color outlineColor = new Color(220, 220, 220);
		if (getModification() == CollectionMod.ADDITION && getCardCount() == 1)
		{
			double prog = getVisibleShiftProgress();
			int progColor = (int) (gray.getRed() - (prog * gray.getRed()));
			textColor = new Color(progColor, progColor, progColor);
			outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 255 - (int) (prog * 255));
		}
		else if (getModification() == CollectionMod.REMOVAL && getCardCount() == 0)
		{
			double prog = getVisibleShiftProgress();
			int progColor = (int) (prog * gray.getRed());
			textColor = new Color(progColor, progColor, progColor);
			outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), (int) (prog * 255));
		}
		else if (getCardCount() == 0)
		{
			textColor = gray;
		}
		if ((getCardCount() == 1 && getModification() == CollectionMod.ADDITION) || getCardCount() == 0)
		{
			g.setColor(outlineColor);
			g.fillRoundRect(scale(0), scale(20), getWidth(), getHeight() - scale(20), scale(15), scale(15));
		}
		g.setColor(textColor);
		TextPainter tp = new TextPainter("BANK", GraphicsUtils.getBoldMDFont(scale(18)), new Rectangle(0, scale(1), getWidth() - scale(5), scale(20)));
		tp.setHorizontalAlignment(Alignment.RIGHT);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		CardCollection bank = getCollection();
		
		if (!bank.isEmpty())
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			paintCards(g);
		}
		if (getClient().isDebugEnabled())
		{
			debug.setColor(Color.GREEN);
			GraphicsUtils.drawDebug(debug, "ID: " + getCollection().getID(), scale(15), getWidth(), getHeight() + scale(25));
		}
	}
	
	@Override
	public void paintCards(Graphics2D g)
	{
		for (Entry<Card, Point> entry : getCurrentCardPositions().entrySet())
		{
			Card card = entry.getKey();
			if (card == hovered && !isBeingModified())
			{
				continue;
			}
			Point p = entry.getValue();
			g.drawImage(card.getGraphics(getScale() * getCardScale()), p.x, p.y, GraphicsUtils.getCardWidth(getCardScale()),
					GraphicsUtils.getCardHeight(getCardScale()), null);
		}
		if (hovered != null && !isBeingModified())
		{
			Point p = getLocationOf(hovered);
			g.drawImage(hovered.getGraphics(GraphicsUtils.SCALE), p.x, p.y - scale(10), null);
		}
	}
	
	public double getInterval(int cardCount)
	{
		return cardCount == 1 ? 0 : Math.min(scale(30), (getWidth() - GraphicsUtils.getCardWidth()) / 
				(double) (cardCount - 1));
	}
	
	public double getInterval()
	{
		return getInterval(getCollection().getCardCount());
	}

	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(getWidth() - GraphicsUtils.getCardWidth() - (int) (cardIndex * getInterval(cardCount)), scale(20));
	}
}
