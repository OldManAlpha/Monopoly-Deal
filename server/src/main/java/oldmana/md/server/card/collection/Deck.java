package oldmana.md.server.card.collection;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.deck.DeckStack;
import oldmana.md.server.event.DeckReshuffledEvent;

public class Deck extends CardCollection
{
	private DeckStack stack;
	
	private Random rand = new Random();
	
	public Deck(DeckStack stack)
	{
		super(null);
		setDeckStack(stack);
	}
	
	public void setDeckStack(DeckStack stack)
	{
		setDeckStack(stack, true);
	}
	
	public void setDeckStack(DeckStack stack, boolean shuffle)
	{
		if (this.stack != null)
		{
			for (Card card : this.stack.getCards())
			{
				card.transfer(getServer().getVoidCollection(), -1, 15);
			}
		}
		this.stack = stack;
		for (Card card : stack.getCards())
		{
			card.transfer(this, -1, 15);
		}
		if (shuffle)
		{
			shuffle();
		}
	}
	
	public DeckStack getDeckStack()
	{
		return stack;
	}
	
	public void shuffle()
	{
		Collections.shuffle(getCards());
	}
	
	public Card drawCard(Player player)
	{
		return drawCard(player, 1);
	}
	
	public Card drawCard(Player player, double speed)
	{
		if (getCardCount() > 0)
		{
			Card card = getCards().get(0);
			transferCard(card, player.getHand(), 0, speed);
			return card;
		}
		else
		{
			List<Card> cards = getServer().getDiscardPile().getCards(true);
			Collections.reverse(cards);
			for (Card card : cards)
			{
				card.transfer(this, 0, 4);
			}
			shuffle();
			getServer().getEventManager().callEvent(new DeckReshuffledEvent(this));
			if (getCardCount() == 0) // That'd be kinda bad..
			{
				System.out.println("Deck and discard pile are out of cards!");
				return null;
			}
			return drawCard(player, speed);
		}
	}
	
	public void drawCards(Player player, int amount)
	{
		drawCards(player, amount, 1);
	}
	
	public void drawCards(Player player, int amount, double speed)
	{
		for (int i = 0 ; i < amount ; i++)
		{
			drawCard(player, speed);
		}
	}
	
	public void insertCardRandomly(Card card)
	{
		insertCardRandomly(card, 1);
	}
	
	public void insertCardRandomly(Card card, double speed)
	{
		card.transfer(this, rand.nextInt(getCardCount()), speed);
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
