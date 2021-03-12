package oldmana.md.client.gui.component;

import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDCardGroup extends MDComponent
{
	private List<MDCard> cards;
	
	private double cardScale;
	
	public MDCardGroup()
	{
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	private List<MDCard> cards;
	
	private Dimension size;
	
	private double cardScale = 1;
	
	private double interval = 1.1;
	private double rowInterval = 1.1;
	
	private boolean vertical;
	
	private CardAlignment alignment;
	
	public MDCardGroup()
	{
		
	}
	
	public MDCard getUIByCard(Card card)
	{
		return null;
	}
	
	public Dimension calculateComponentSize()
	{
		int w;
		int h;
		int cardCount = cards.size();
		int cardWidth = GraphicsUtils.getCardWidth(cardScale);
		int cardHeight = GraphicsUtils.getCardHeight(cardScale);
		
		return null;
	}
	
	private int getMaxCardsInRow()
	{
		if ((vertical && size.getHeight() < 0) || (!vertical && size.getWidth() < 0))
		{
			return -1;
		}
		int rowSize = (int) (vertical ? size.getHeight() : size.getWidth());
		int cardSize = vertical ? GraphicsUtils.getCardHeight(cardScale) : GraphicsUtils.getCardWidth(cardScale);
		
		int cards = 1 + (int) ((rowSize - cardSize) / (cardSize * (interval * GraphicsUtils.getCardWidth())));
		
		return 0;
	}
	
	public double getInterval()
	{
		double maxCards = getMaxCardsInRow();
		if (cards.size() > maxCards)
		{
			return (getRowSize() - getCardSize()) / maxCards;
		}
		else if (alignment == CardAlignment.CENTER)
		{
			
		}
		return 0;
	}
	
	private int getRowSize()
	{
		return (int) (vertical ? size.getHeight() : size.getWidth());
	}
	
	private int getCardSize()
	{
		return vertical ? GraphicsUtils.getCardHeight(cardScale) : GraphicsUtils.getCardWidth(cardScale);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension();
	}
	
	public class CardGroupLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container arg0)
		{
			
		}
		
		@Override
		public void invalidateLayout(Container target)
		{
			layoutContainer(target);
		}
	}
	
	public static enum CardAlignment
	{
		LEFT, CENTER, RIGHT
	}
	
	public static enum CardLayout
	{
		LIST, LIST_CENTERED, PROPERTY_SET
	}
	*/
}
