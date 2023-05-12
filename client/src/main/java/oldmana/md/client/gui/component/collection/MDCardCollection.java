package oldmana.md.client.gui.component.collection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.util.GraphicsUtils;

public abstract class MDCardCollection extends MDCardCollectionBase
{
	private Card modifiedCard;
	
	public MDCardCollection(CardCollection collection, double scale)
	{
		super(collection, scale);
		update();
	}
	
	public MDCardCollection(CardCollection collection)
	{
		this(collection, 1);
	}
	
	public void startAddition(Card card, int index)
	{
		super.startAddition(index);
		modifiedCard = card;
	}
	
	public void startRemoval(Card card)
	{
		super.startRemoval(getCollection().getIndexOf(card));
		modifiedCard = card;
	}
	
	@Override
	public void startAddition(int index)
	{
		//startAddition(getCollection().getCardAt(index));
	}
	
	@Override
	public void startRemoval(int index)
	{
		//startRemoval(getCollection().getCardAt(index));
	}
	
	public Card getModifiedCard()
	{
		return modifiedCard;
	}
	
	public List<Card> getPreviousCards()
	{
		List<Card> cards = new ArrayList<Card>(getCollection().getCards());
		if (getModification() == CollectionMod.ADDITION)
		{
			cards.remove(getModIndex());
		}
		else
		{
			cards.add(getModIndex(), getModifiedCard());
		}
		return cards;
	}
	
	@Override
	public void cardArrived()
	{
		super.cardArrived();
		modifiedCard = null;
		update();
	}
	
	public Point getScreenLocationOf(Card card)
	{
		return SwingUtilities.convertPoint(this, getLocationOf(card), getClient().getWindow().getTableScreen());
	}
	
	/*
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return getLocationOf(getCollection().getCardAt(cardIndex));
	}
	*/
	
	public Point getLocationOf(Card card)
	{
		return getLocationOf(card, getCollection().getCardCount());
	}
	
	public Point getLocationOf(Card card, int cardCount)
	{
		return getLocationOf(getCollection().getIndexOf(card), cardCount);
	}
	
	public Point getLocationOf(Card card, List<Card> cards)
	{
		return getLocationOf(cards.indexOf(card), cards.size());
	}
	
	public MDSelection createSelectionOf(Card card)
	{
		MDSelection selection = new MDSelection();
		selection.setLocation(getScreenLocationOf(card));
		selection.setSize(GraphicsUtils.getCardWidth(getCardScale()), GraphicsUtils.getCardHeight(getCardScale()));
		return selection;
	}
	
	public MDCard createViewOf(Card card)
	{
		MDCard view = new MDCard(card, getCardScale());
		view.setLocation(getScreenLocationOf(card));
		return view;
	}
	
	//public abstract Point getLocationOf(int index, List<Card> cards);
	
	public Map<Card, Point> getCurrentCardPositions()
	{
		Map<Card, Point> positions = new LinkedHashMap<Card, Point>();
		if (getModification() != null)
		{
			List<Card> cards = getCollection().getCards();
			List<Card> prevCards = getPreviousCards();
			
			Map<Card, Point> curPositions = new LinkedHashMap<Card, Point>();
			Map<Card, Point> prevPositions = new LinkedHashMap<Card, Point>();
			
			for (int i = 0 ; i < cards.size() ; i++)
			{
				Card card = cards.get(i);
				curPositions.put(card, getLocationOf(i, cards.size()));
			}
			
			for (int i = 0 ; i < prevCards.size() ; i++)
			{
				Card card = prevCards.get(i);
				prevPositions.put(card, getLocationOf(i, prevCards.size()));
			}
			
			for (Entry<Card, Point> entry : curPositions.entrySet())
			{
				if (prevPositions.containsKey(entry.getKey()))
				{
					Point p2 = prevPositions.get(entry.getKey());
					Point p1 = entry.getValue();
					positions.put(entry.getKey(), new Point((int) (p2.x + ((p1.x - p2.x) * getVisibleShiftProgress())), 
							(int) (p2.y + ((p1.y - p2.y) * getVisibleShiftProgress()))));
				}
			}
			
			/*
			boolean shift = false;
			for (int i = 0 ; i < cards.size() ; i++)
			{
				Card card = cards.get(i);
				if (!prevCards.contains(card))
				{
					shift = true;
					continue;
				}
				Point p1 = getLocationOf(i - (shift ? 1 : 0), cards.size());
				Point p2 = getLocationOf(i, prevCards.size());
				System.out.println("p1: " + p1);
				System.out.println("p2: " + p2);
				System.out.println(this.getModIndex());
				positions.put(card, new Point((int) (p2.x + ((p1.x - p2.x) * (Math.min(1, getCardMoveProgress() * 1.5)))), 
						(int) (p2.y + ((p1.y - p2.y) * (Math.min(1, getCardMoveProgress() * 1.5))))));
			}
			*/
		}
		else
		{
			for (Card card : getCollection().getCards())
			{
				Point p = getLocationOf(card);
				positions.put(card, p);
			}
		}
		return positions;
	}
	
	public void paintCards(Graphics2D g)
	{
		for (Entry<Card, Point> entry : getCurrentCardPositions().entrySet())
		{
			Card card = entry.getKey();
			Point p = entry.getValue();
			g.drawImage(card.getGraphics(getScale() * getCardScale()), p.x, p.y, GraphicsUtils.getCardWidth(getCardScale()), 
					GraphicsUtils.getCardHeight(getCardScale()), null);
		}
	}
}
