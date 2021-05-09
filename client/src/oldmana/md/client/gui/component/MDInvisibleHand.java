package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
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
	
	/*
	@Override
	public void addCard(MDCard card)
	{
		
	}
	
	@Override
	public void removeCard(MDCard card)
	{
		
	}
	*/
	
	@Override
	public void update()
	{
		/*
		int currentCount = getCollection().getCardCount();
		int prevCount = getMDCards().size();
		if (currentCount > prevCount)
		{
			for (int i = 0 ; i < currentCount - prevCount ; i++)
			{
				MDCard card = new MDCard(null);
				add(card);
				getMDCards().add(card);
			}
		}
		else if (currentCount < prevCount)
		{
			for (int i = 0 ; i < prevCount - currentCount ; i++)
			{
				MDCard card = getMDCards().remove(0);
				remove(card);
			}
		}
		
		if (getMDCards().size() > 0)
		{
			int interval = getMDCards().size() == 1 ? 0 : Math.min(30, (getWidth() - MDCard.CARD_SIZE.width) / (getMDCards().size() - 1));
			int start = 0;
			for (int i = 0 ; i < getMDCards().size() ; i++)
			{
				getMDCards().get(i).setLocation(start + (interval * i), 0);
			}
		}
		*/
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		//g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
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
			double interval = getInterval();
			BufferedImage back = Card.getBackGraphics(GraphicsUtils.SCALE);
			for (int i = 0 ; i < hand.getCardCount() - (isCardIncoming() ? 1 : 0) ; i++)
			{
				//CardPainter cp = new CardPainter(null, scale(1));
				g.translate((int) (interval * i), 0);
				//cp.paint(g);
				g.drawImage(back, 0, 0, back.getWidth(), getHeight(), null);
				g.translate((int) -(interval * i), 0);
				
				//getMDCards().get(i).setLocation(start + (interval * i), 0);
			}
		}
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.MAGENTA);
			GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(20), getWidth(), getHeight());
		}
	}
	
	public double getInterval()
	{
		return getCollection().getCardCount() == 1 ? 0 : Math.min(scale(30), (getWidth() - GraphicsUtils.getCardWidth()) / 
				(double) (getCollection().getCardCount() - 1));
	}

	@Override
	public Point getLocationOf(int cardIndex)
	{
		return SwingUtilities.convertPoint(this, new Point((int) (getInterval() * (getCollection().getCardCount() - 1)), 0), getClient().getTableScreen());
	}
}
