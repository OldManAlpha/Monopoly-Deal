package oldmana.general.md.server.card.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import oldmana.general.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardMoney;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.md.server.card.CardProperty.PropertyColor;
import oldmana.general.md.server.card.action.CardActionBirthday;
import oldmana.general.md.server.card.action.CardActionDealBreaker;
import oldmana.general.md.server.card.action.CardActionDebtCollector;
import oldmana.general.md.server.card.action.CardActionDoubleTheRent;
import oldmana.general.md.server.card.action.CardActionForcedDeal;
import oldmana.general.md.server.card.action.CardActionGo;
import oldmana.general.md.server.card.action.CardActionJustSayNo;
import oldmana.general.md.server.card.action.CardActionRent;
import oldmana.general.md.server.card.action.CardActionSlyDeal;
import oldmana.general.md.server.card.collection.deck.DeckStack;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class Deck extends CardCollection
{
	private DeckStack stack;
	
	public Deck(DeckStack stack)
	{
		super(null);
		setDeckStack(stack);
	}
	
	public void setDeckStack(DeckStack stack)
	{
		if (this.stack != null)
		{
			for (Card card : this.stack.getCards())
			{
				card.transfer(getServer().getVoidCollection(), -1, 12);
			}
		}
		this.stack = stack;
		for (Card card : stack.getCards())
		{
			card.transfer(this, -1, 12);
		}
		shuffle();
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
			//int size = getServer().getDiscardPile().getCardCount();
			List<Card> cards = new ArrayList<Card>(getServer().getDiscardPile().getCards());
			Collections.reverse(cards);
			for (Card card : cards)
			{
				card.transfer(this, 0, 3);
			}
			shuffle();
			if (getCardCount() == 0)
			{
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
	
	public Card peek(int index)
	{
		if (getCardCount() > index)
		{
			return getCards().get(index);
		}
		return null;
	}
	
	/*
	@Override
	public void transferCard(Card card, CardCollection to)
	{
		super.transferCard(card, to, 0);
		PacketMoveUnknownCard packet = new PacketMoveUnknownCard(getID(), to.getID());
		
		if (to.getOwner() != null)
		{
			getServer().broadcastPacket(packet, to.getOwner());
			PacketMoveRevealCard packet2 = new PacketMoveRevealCard(card.getID(), getID(), to.getID(), to.getIndexOf(card));
			to.getOwner().sendPacket(packet2);
		}
		else
		{
			getServer().broadcastPacket(packet);
		}
	}
	*/
	
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
