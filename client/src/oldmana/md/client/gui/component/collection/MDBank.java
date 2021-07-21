package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.card.collection.Bank;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDBank extends MDCardCollection
{
	public MDBank(Bank bank)
	{
		super(bank);
	}
	
	@Override
	public void update()
	{
		/*
		if (getMDCards().size() > 0)
		{
			double interval = getMDCards().size() == 1 ? 0 : Math.min(30, (double) (getWidth() - MDCard.CARD_SIZE.width) / (double) (getMDCards().size() - 1));
			int start = 0;
			for (int i = 0 ; i < getMDCards().size() ; i++)
			{
				getMDCards().get(i).setLocation((int) (start + (interval * i)), 0);
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
		Graphics2D debug = (Graphics2D) g.create();
		g.setColor(Color.DARK_GRAY);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(15), scale(15));
		g.drawLine(0, scale(20), getWidth(), scale(20));
		g.setColor(Color.BLACK);
		TextPainter btp = new TextPainter("BANK", GraphicsUtils.getBoldMDFont(scale(18)), new Rectangle(0, scale(4), getWidth(), scale(20)));
		btp.setHorizontalAlignment(Alignment.CENTER);
		btp.setVerticalAlignment(Alignment.CENTER);
		btp.paint(g);
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
			GraphicsUtils.drawDebug(debug, "ID: " + getCollection().getID(), scale(20), getWidth(), getHeight());
		}
	}
	
	public double getInterval(int cardCount)
	{
		return cardCount == 1 ? 0 : Math.min(scale(30), (getWidth() - scale(10) - GraphicsUtils.getCardWidth()) / 
				(double) (cardCount - 1));
	}
	
	public double getInterval()
	{
		return getInterval(getCollection().getCardCount());
	}

	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point((int) (cardIndex * getInterval(cardCount)) + scale(5), scale(25));
	}
}
