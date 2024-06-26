package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.common.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.deck.DeckStack;
import oldmana.md.server.event.card.DeckReshuffledEvent;

public class Deck extends CardCollection
{
	private Map<String, DeckStack> stacks = new HashMap<String, DeckStack>();
	
	private DeckStack stack;
	
	private Random rand = new Random();
	
	public Deck()
	{
		super(null);
	}
	
	public void registerDeckStack(String name, DeckStack stack)
	{
		stacks.put(name, stack);
	}
	
	public DeckStack unregisterDeckStack(String name)
	{
		return stacks.remove(name);
	}
	
	public boolean isDeckStackRegistered(String name)
	{
		return stacks.containsKey(name);
	}
	
	public DeckStack getDeckStack(String name)
	{
		return stacks.get(name);
	}
	
	public Map<String, DeckStack> getDeckStacks()
	{
		return stacks;
	}
	
	public void setDeckStack(String name)
	{
		setDeckStack(stacks.get(name));
	}
	
	public void setDeckStack(DeckStack stack)
	{
		setDeckStack(stack, true);
	}
	
	public void setDeckStack(DeckStack stack, boolean shuffle)
	{
		if (this.stack != null)
		{
			for (Card card : getCards(true))
			{
				card.transfer(getServer().getVoidCollection(), -1, 0.05);
			}
		}
		this.stack = stack;
		for (Card card : stack.getCards())
		{
			card.transfer(this, -1, 0.05);
		}
		if (shuffle)
		{
			shuffle();
		}
		getServer().getGameRules().setRules(stack.getDeckRules());
	}
	
	/**
	 * Get the in-use DeckStack.
	 * @return The stack currently used
	 */
	public DeckStack getDeckStack()
	{
		return stack;
	}
	
	public void shuffle()
	{
		shuffle(true);
	}
	
	public void shuffle(boolean playSound)
	{
		Collections.shuffle(getCards());
		getServer().getGameState().setStateChanged();
		if (playSound)
		{
			getServer().playSound("DeckShuffle", true);
		}
	}
	
	/**
	 * Get the top card, reshuffling if needed. This usually should only be used if it is intended to immediately
	 * move the card somewhere.
	 * @return The top card in the deck
	 */
	public Card drawCard()
	{
		if (isEmpty())
		{
			reshuffle();
			if (isEmpty())
			{
				return null;
			}
			return drawCard();
		}
		return getCards().get(0);
	}
	
	public Card drawCard(Player player)
	{
		return drawCard(player, 1);
	}
	
	public Card drawCard(Player player, double time)
	{
		if (isEmpty())
		{
			reshuffle();
			if (isEmpty())
			{
				System.out.println("Deck and discard pile are out of cards! " + player.getName() + " cannot draw a card.");
				return null;
			}
			return drawCard(player, time);
		}
		Card card = getCards().get(0);
		transferCard(card, player.getHand(), 0, time);
		return card;
	}
	
	public List<Card> drawCards(Player player, int amount)
	{
		return drawCards(player, amount, 1);
	}
	
	public List<Card> drawCards(Player player, int amount, double time)
	{
		List<Card> cards = new ArrayList<Card>(amount);
		for (int i = 0 ; i < amount ; i++)
		{
			Card card = drawCard(player, time);
			if (card != null)
			{
				cards.add(card);
			}
		}
		return cards;
	}
	
	public void insertCardRandomly(Card card)
	{
		insertCardRandomly(card, 1);
	}
	
	public void insertCardRandomly(Card card, double time)
	{
		card.transfer(this, rand.nextInt(getCardCount()), time);
	}
	
	/**
	 * Moves all cards from the discard pile into the deck and shuffles.
	 */
	public void reshuffle()
	{
		List<Card> cards = getServer().getDiscardPile().getCards(true);
		Collections.reverse(cards);
		for (Card card : cards)
		{
			card.transfer(this, 0, 0.25);
		}
		shuffle();
		getServer().getEventManager().callEvent(new DeckReshuffledEvent(this));
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return false;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketUnknownCardCollectionData(getID(), -1, getCardCount(), CardCollectionType.DECK);
	}
}
