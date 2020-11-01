package oldmana.md.server.card.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketMoveCard;
import oldmana.md.net.packet.server.PacketMovePropertySet;
import oldmana.md.net.packet.server.PacketMoveRevealCard;
import oldmana.md.net.packet.server.PacketMoveUnknownCard;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.util.IDCounter;

public abstract class CardCollection
{
	private int id;
	
	private List<Card> cards;
	private Player owner;
	
	public CardCollection(Player owner)
	{
		this.owner = owner;
		cards = new ArrayList<Card>();
		
		id = IDCounter.nextCollectionID();
		CardCollectionRegistry.registerCardCollection(this);
	}
	
	public CardCollection(Player owner, List<Card> cards)
	{
		this.owner = owner;
		this.cards = cards;
		for (Card card : cards)
		{
			card.setOwningCollection(this);
		}
		
		id = IDCounter.nextCollectionID();
		CardCollectionRegistry.registerCardCollection(this);
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
	
	public int[] getCardIds()
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
		/*
		if (hasCard(card))
		{
			removeCard(card);
			to.addCard(card);
		}
		*/
	}
	
	public void transferCard(Card card, CardCollection to, int index)
	{
		transferCard(card, to, index, 1);
	}
	
	public void transferCard(Card card, CardCollection to, int index, double speed)
	{
		if (hasCard(card))
		{
			removeCard(card);
			to.addCard(card, index);
		}
		for (Player player : getServer().getPlayers())
		{
			index = to.getIndexOf(card);
			boolean canSeeFrom = isVisibleTo(player);
			boolean canSeeTo = to.isVisibleTo(player);
			Packet packet = null;
			if (canSeeFrom && canSeeTo)
			{
				packet = new PacketMoveCard(card.getID(), to.getID(), index, speed);
			}
			else if (!canSeeFrom && canSeeTo)
			{
				packet = new PacketMoveRevealCard(card.getID(), getID(), to.getID(), index, speed);
			}
			else if (canSeeFrom && !canSeeTo)
			{
				packet = new PacketMoveCard(card.getID(), to.getID(), index, speed);
			}
			else if (!canSeeFrom && !canSeeTo)
			{
				packet = new PacketMoveUnknownCard(getID(), to.getID(), speed);
			}
			player.sendPacket(packet);
		}
	}
	
	public void setOwner(Player player)
	{
		owner = player;
		getServer().broadcastPacket(new PacketMovePropertySet(getID(), player.getID(), -1));
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean hasOwner()
	{
		return owner != null;
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public abstract boolean isVisibleTo(Player player);
	
	public abstract Packet getCollectionDataPacket();
}
