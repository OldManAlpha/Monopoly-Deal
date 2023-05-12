package oldmana.md.server.card.collection.deck;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.Card;
import oldmana.md.server.rules.GameRule;

public abstract class DeckStack
{
	private List<Card> cards = new ArrayList<Card>();
	private GameRule deckRules;
	
	public DeckStack()
	{
		createDeck();
	}
	
	public abstract void createDeck();
	
	public void addCard(Card card)
	{
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
	
	public GameRule getDeckRules()
	{
		return new GameRule(deckRules);
	}
	
	public void setDeckRules(GameRule deckRules)
	{
		this.deckRules = deckRules;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
