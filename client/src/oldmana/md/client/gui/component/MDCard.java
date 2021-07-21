package oldmana.md.client.gui.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDCard extends MDComponent
{
	public static Dimension CARD_SIZE = new Dimension(60, 90);
	
	private Card card;
	
	private double size;
	
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
	}
}
