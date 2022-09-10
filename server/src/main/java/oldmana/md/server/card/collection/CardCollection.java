package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketMoveCard;
import oldmana.md.net.packet.server.PacketMoveRevealCard;
import oldmana.md.net.packet.server.PacketMoveUnknownCard;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAnimationType;
import oldmana.md.server.event.CardMovedEvent;

public abstract class CardCollection implements Iterable<Card>
{
	private static Map<Integer, CardCollection> collections = new HashMap<Integer, CardCollection>();
	
	private static int nextID;
	
	
	private int id;
	
	private List<Card> cards;
	private Player owner;
	
	public CardCollection(Player owner)
	{
		this.owner = owner;
		cards = new ArrayList<Card>();
		
		id = nextID++;
		registerCardCollection(this);
	}
	
	public CardCollection(Player owner, List<Card> cards)
	{
		this.owner = owner;
		this.cards = cards;
		for (Card card : cards)
		{
			card.setOwningCollection(this);
		}
		
		id = nextID++;
		registerCardCollection(this);
	}
	
	public int getID()
	{
		return id;
	}
	
	public void addCard(Card card)
	{
		cards.add(card);
		card.setOwningCollection(this);
	}
	
	public void addCard(Card card, int index)
	{
		cards.add(index > -1 ? index : cards.size(), card);
		card.setOwningCollection(this);
	}
	
	public void removeCard(Card card)
	{
		cards.remove(card);
	}
	
	public boolean hasCard(Card card)
	{
		return cards.contains(card);
	}
	
	@Override
	public Iterator<Card> iterator()
	{
		return cards.iterator();
	}
	
	public List<Card> getCards()
	{
		return cards;
	}
	
	public List<Card> getCards(boolean copy)
	{
		return copy ? new ArrayList<Card>(cards) : cards;
	}
	
	public List<Card> getCardsInReverse()
	{
		List<Card> cards = new ArrayList<Card>(this.cards);
		Collections.reverse(cards);
		return cards;
	}
	
	public Card getCardAt(int index)
	{
		return cards.get(index);
	}
	
	public int getIndexOf(Card card)
	{
		return cards.indexOf(card);
	}
	
	public int[] getCardIDs()
	{
		int[] ids = new int[getCardCount()];
		for (int i = 0 ; i < ids.length ; i++)
		{
			ids[i] = cards.get(i).getID();
		}
		return ids;
	}
	
	public int getCardCount()
	{
		return cards.size();
	}
	
	public boolean isEmpty()
	{
		return cards.isEmpty();
	}
	
	public void transferCard(Card card, CardCollection to)
	{
		transferCard(card, to, -1);
	}
	
	public void transferCard(Card card, CardCollection to, int index)
	{
		transferCard(card, to, index, 1);
	}
	
	public void transferCard(Card card, CardCollection to, int index, double time)
	{
		transferCard(card, to, index, time, false);
	}
	
	public void transferCard(Card card, CardCollection to, int index, CardAnimationType anim)
	{
		transferCard(card, to, index, anim, false);
	}
	
	public void transferCard(Card card, CardCollection to, int index, CardAnimationType anim, boolean flash)
	{
		transferCard(card, to, index, getDefaultTransferTime(anim), anim, flash);
	}
	
	public void transferCard(Card card, CardCollection to, int index, double time, CardAnimationType anim)
	{
		transferCard(card, to, index, time, anim, false);
	}
	
	public void transferCard(Card card, CardCollection to, int index, double time, boolean flash)
	{
		transferCard(card, to, index, time, flash ? CardAnimationType.IMPORTANT : CardAnimationType.NORMAL, flash);
	}
	
	public void transferCard(Card card, CardCollection to, int index, double time, CardAnimationType anim, boolean flash)
	{
		if (hasCard(card))
		{
			removeCard(card);
			to.addCard(card, index);
		}
		for (Player player : getServer().getPlayers())
		{
			player.sendPacket(getCardTransferPacket(player, card, to, time, anim, flash));
		}
		getServer().getEventManager().callEvent(new CardMovedEvent(card, this, to, index, time));
		getServer().getGameState().setStateChanged();
	}
	
	public void flashCard(Card card, double time, CardAnimationType anim)
	{
		for (Player player : getServer().getPlayers())
		{
			player.sendPacket(getCardTransferPacket(player, card, this, time, anim, true));
		}
	}
	
	public Packet getCardTransferPacket(Player viewer, Card card, CardCollection to, double time,
	                                    CardAnimationType anim, boolean flash)
	{
		int fromIndex = to.getIndexOf(card);
		boolean canSeeFrom = isVisibleTo(viewer);
		boolean canSeeTo = to.isVisibleTo(viewer);
		Packet packet = null;
		if (canSeeFrom)
		{
			packet = new PacketMoveCard(card.getID(), to.getID(), fromIndex, time, anim.getID());
		}
		else if ((!canSeeFrom && canSeeTo) || flash)
		{
			packet = new PacketMoveRevealCard(card.getID(), getID(), to.getID(), fromIndex, time, anim.getID());
		}
		else if (!canSeeFrom && !canSeeTo)
		{
			packet = new PacketMoveUnknownCard(getID(), to.getID(), time, anim.getID());
		}
		return packet;
	}
	
	public double getDefaultTransferTime()
	{
		return getDefaultTransferTime(CardAnimationType.NORMAL);
	}
	
	public double getDefaultTransferTime(CardAnimationType anim)
	{
		switch (anim)
		{
			case NORMAL: return 1;
			case IMPORTANT: return 3;
			default: return 1;
		}
	}
	
	public void setOwner(Player player)
	{
		owner = player;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean hasOwner()
	{
		return owner != null;
	}
	
	protected MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract boolean isVisibleTo(Player player);
	
	public abstract Packet getCollectionDataPacket();
	
	
	public static void registerCardCollection(CardCollection collection)
	{
		collections.put(collection.getID(), collection);
	}
	
	public static Map<Integer, CardCollection> getRegisteredCardCollections()
	{
		return collections;
	}
	
	public static CardCollection getCardCollection(int id)
	{
		return collections.get(id);
	}
}
