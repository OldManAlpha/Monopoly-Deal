package oldmana.md.server.card.collection.deck;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.Card;

public abstract class DeckStack
{
	private List<Card> cards = new ArrayList<Card>();
	
	public DeckStack()
	{
		createDeck();
	}
	
	public abstract void createDeck();
	
	public void addCard(Card card)
	{
		getServer().getVoidCollection().addCard(card);
		cards.add(card);
	}
	
	public void removeCard(Card card)
	{
		cards.remove(card);
	}
	
	public boolean hasCard(Card card)
	{
		return cards.contains(card);
	}
	
	public List<Card> getCards()
	{
		return cards;
	}
	
	public void broadcastCardPackets()
	{
		for (Card card : cards)
		{
			getServer().broadcastPacket(card.getCardDataPacket());
		}
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
