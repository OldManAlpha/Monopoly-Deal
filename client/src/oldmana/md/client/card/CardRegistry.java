package oldmana.md.client.card;

import java.util.ArrayList;
import java.util.List;

public class CardRegistry
{
	public static List<Card> cards = new ArrayList<Card>();
	
	public static Card getCard(int id)
	{
		for (Card card : cards)
		{
			if (card.getID() == id)
			{
				return card;
			}
		}
		return null;
	}
	
	public static List<Card> getCards(int[] ids)
	{
		List<Card> cards = new ArrayList<Card>();
		for (int id : ids)
		{
			cards.add(getCard(id));
		}
		return cards;
	}
	
	public static List<CardProperty> getPropertyCards(int[] ids)
	{
		List<CardProperty> props = new ArrayList<CardProperty>();
		List<Card> cards = getCards(ids);
		for (Card card : cards)
		{
			props.add((CardProperty) card);
		}
		return props;
	}
	
	public static void registerCard(Card card)
	{
		cards.add(card);
	}
	
	public static List<Card> getRegisteredCards()
	{
		return cards;
	}
}
