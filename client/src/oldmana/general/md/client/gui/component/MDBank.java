package oldmana.general.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.general.md.client.ThePlayer;
import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.collection.Bank;
import oldmana.general.md.client.card.collection.CardCollection;
import oldmana.general.md.client.gui.util.CardPainter;
import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.gui.util.TextPainter;
import oldmana.general.md.client.gui.util.TextPainter.Alignment;

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
		g.setColor(Color.DARK_GRAY);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(15), scale(15));
		g.drawLine(0, scale(20), getWidth(), scale(20));
		g.setColor(Color.BLACK);
		TextPainter btp = new TextPainter("BANK", GraphicsUtils.getBoldMDFont(scale(18)), new Rectangle(0, scale(4), getWidth(), scale(20)));
		btp.setHorizontalAlignment(Alignment.CENTER);
		btp.setVerticalAlignment(Alignment.CENTER);
		btp.paint(g);
		CardCollection bank = getCollection();
		g.translate(scale(5), scale(25));
		if (bank.isEmpty())
		{
			/*
			g.setColor(Color.DARK_GRAY);
			Font font = new Font(getFont().getFontName(), Font.BOLD, 16);
			g.setFont(font);
			TextPainter tp = new TextPainter("Bank Empty", font, new Rectangle(0, 0, getWidth(), getHeight()));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
			*/
		}
		else
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//double interval = bank.getCardCount() == 1 ? 0 : Math.min(scale(30), (getWidth() - scale(10) - GraphicsUtils.getCardWidth()) / 
			//		(double) (bank.getCardCount() - 1));
			double interval = getInterval();
			for (int i = 0 ; i < bank.getCardCount() ; i++)
			{
				g.translate((int) (interval * i), 0);
				if (bank.getCardAt(i) != getIncomingCard())
				{
					g.drawImage(bank.getCardAt(i).getGraphics(getScale()), 0, 0, GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight(), null);
				}
				g.translate((int) -(interval * i), 0);
				
				//getMDCards().get(i).setLocation(start + (interval * i), 0);
			}
		}
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.GREEN);
			GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), 20, getWidth(), getHeight());
		}
	}
	
	public double getInterval()
	{
		return getCollection().getCardCount() == 1 ? 0 : Math.min(scale(30), (getWidth() - scale(10) - GraphicsUtils.getCardWidth()) / 
				(double) (getCollection().getCardCount() - 1));
	}

	@Override
	public Point getLocationInComponentOf(Card card)
	{
		return new Point((int) (getCollection().getIndexOf(card) * getInterval()) + scale(5), scale(25));
	}
}
