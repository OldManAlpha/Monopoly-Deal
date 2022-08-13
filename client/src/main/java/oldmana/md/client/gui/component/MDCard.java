package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDCard extends MDComponent
{
	public static Dimension CARD_SIZE = new Dimension(60, 90);
	
	private Card card;
	
	private double size;
	
	private String banner;
	
	public MDCard(Card card)
	{
		this.card = card;
		setSize(1);
	}
	
	public MDCard(Card card, double size)
	{
		this.card = card;
		setSize(size);
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public void setSize(double size)
	{
		this.size = size;
		updateSize();
	}
	
	public void setBanner(String text)
	{
		banner = text;
	}
	
	public void updateSize()
	{
		setSize(GraphicsUtils.getCardWidth(size), GraphicsUtils.getCardHeight(size));
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.drawImage(card.getGraphics(GraphicsUtils.SCALE * size), 0, 0, getWidth(), getHeight(), null);
		if (banner != null)
		{
			g = (Graphics2D) g.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.rotate(Math.toRadians(-30), getWidth() / 2, getHeight() / 2);
			g.setColor(Color.GRAY);
			g.fillRect(scale(-20), getHeight() / 2 - scale(15), getWidth() + scale(40), scale(30));
			g.setColor(Color.BLACK);
			g.drawRect(scale(-20), getHeight() / 2 - scale(15), getWidth() + scale(40), scale(30));
			TextPainter tp = new TextPainter(banner, GraphicsUtils.getBoldMDFont(scale(12 * size)), new Rectangle(0, 0, getWidth(), getHeight()));
			tp.setHorizontalAlignment(Alignment.CENTER);
			tp.setVerticalAlignment(Alignment.CENTER);
			tp.paint(g);
		}
	}
}
