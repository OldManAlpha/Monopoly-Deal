package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDCardView extends MDComponent
{
	private List<Card> cards;
	private Color background = Color.GRAY;
	
	public MDCardView(List<Card> cards)
	{
		this.cards = cards;
		setSize(GraphicsUtils.getCardWidth(2.2) * cards.size(), GraphicsUtils.getCardHeight(2) + GraphicsUtils.getCardWidth(0.2));
	}
	
	public MDCardView(List<Card> cards, Color background)
	{
		this(cards);
		this.background = background;
	}
	
	public MDCardView(Card card)
	{
		List<Card> list = new ArrayList<Card>();
		list.add(card);
		this.cards = list;
		setSize(GraphicsUtils.getCardWidth(2.2) * cards.size(), GraphicsUtils.getCardHeight(2) + GraphicsUtils.getCardWidth(0.2));
	}
	
	public MDCardView(Card card, Color background)
	{
		this(card);
		this.background = background;
	}
	
	public double getInterval()
	{
		return GraphicsUtils.getCardWidth(2.2);
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		Color border = background.darker().darker();
		g.setColor(background);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(border);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		g.translate(GraphicsUtils.getCardWidth(0.1), GraphicsUtils.getCardWidth(0.1));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		for (int i = 0 ; i < cards.size() ; i++)
		{
			g.translate((int) (getInterval() * i), 0);
			g.drawImage(cards.get(i).getGraphics(getScale() * 2), 0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), null);
			g.translate((int) -(getInterval() * i), 0);
		}
	}
}
