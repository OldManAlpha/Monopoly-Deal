package oldmana.md.client.card.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.MDEventQueue.CardMove;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardUnknown;
import oldmana.md.client.gui.component.MDMovingCard.CardAnimationType;
import oldmana.md.client.gui.component.collection.MDCardCollectionBase;

public abstract class CardCollection
{
	private static Map<Integer, CardCollection> collections = new HashMap<Integer, CardCollection>();
	
	
	private int id;
	
	private Player owner;
	
	private List<Card> cards;
	
	private boolean unknown;
	
	private MDCardCollectionBase ui;
	
	public CardCollection(int id, Player owner)
	{
		this.owner = owner;
		cards = new ArrayList<Card>();
		
		this.id = id;
		registerCardCollection(this);
	}
	
	public CardCollection(int id, Player owner, List<Card> cards)
	{
		this.owner = owner;
		this.cards = cards;
		for (Card card : cards)
		{
			card.setOwningCollection(this);
		}
		
		this.id = id;
		registerCardCollection(this);
	}
	
	public CardCollection(int id, Player owner, int unknownCards)
	{
		this.owner = owner;
		this.id = id;
		this.unknown = true;
		cards = new ArrayList<Card>();
		addUnknownCards(unknownCards);
		registerCardCollection(this);
	}
	
	public CardCollection(int id, boolean unknown)
	{
		this.owner = null;
		this.id = id;
		this.unknown = unknown;
		cards = new ArrayList<Card>();
		registerCardCollection(this);
	}
	
	public int getID()
	{
		return id;
	}
	
	public boolean isUnknown()
	{
		return unknown;
	}
	
	public void addUnknownCard()
	{
		addUnknownCards(1);
	}
	
	public void addUnknownCards(int amount)
	{
		for (int i = 0 ; i < amount ; i++)
		{
			cards.add(new CardUnknown());
		}
		if (ui != null)
		{
			ui.update();
		}
	}
	
	public void removeUnknownCard()
	{
		removeUnknownCards(1);
	}
	
	public void removeUnknownCards(int amount)
	{
		for (int i = 0 ; i < amount ; i++)
		{
			cards.remove(cards.size() - 1);
		}
		if (ui != null)
		{
			ui.update();
		}
	}
	
	public void addCard(Card card)
	{
		addCardAtIndex(card, -1);
	}
	
	public void addCardAtIndex(Card card, int index)
	{
		cards.add(index > -1 ? index : cards.size(), card);
		card.setOwningCollection(this);
		if (ui != null)
		{
			ui.update();
		}
	}
	
	public void removeCard(Card card)
	{
		cards.remove(card);
		//ui.removeCard(card.getUI());
		if (ui != null)
		{
			ui.update();
		}
	}
	
	public boolean hasCard(Card card)
	{
		return cards.contains(card);
	}
	
	public Card getCardAt(int index)
	{
		return cards.get(index);
	}
	
	public int getIndexOf(Card card)
	{
		return cards.indexOf(card);
	}
	
	public List<Card> getCards()
	{
		return cards;
	}
	
	public int getCardCount()
	{
		return cards.size();
	}
	
	public boolean isEmpty()
	{
		return getCardCount() == 0;
	}
	
	public void transferCardTo(Card card, CardCollection to, int index)
	{
		getClient().getEventQueue().addTask(new CardMove(card, this, to, index));
	}
	
	public void transferCardTo(Card card, CardCollection to, int index, double time, CardAnimationType anim)
	{
		getClient().getEventQueue().addTask(new CardMove(card, this, to, index, time, anim));
	}
	
	public void transferCard(Card card, int index)
	{
		transferCard(card, index, 1, CardAnimationType.NORMAL);
	}
	
	public void transferCard(Card card, int index, double time, CardAnimationType anim)
	{
		getClient().getEventQueue().addTask(new CardMove(card, null, this, index, time, anim));
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
	
	public void setUI(MDCardCollectionBase ui)
	{
		this.ui = ui;
		if (ui != null)
		{
			ui.setCollection(this);
		}
	}
	
	public MDCardCollectionBase getUI()
	{
		return ui;
	}
	
	public boolean makesPlaceSound()
	{
		return true;
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
	
	
	public static Map<Integer, CardCollection> getRegisteredCardCollections()
	{
		return collections;
	}
	
	public static void registerCardCollection(CardCollection collection)
	{
		collections.put(collection.getID(), collection);
	}
	
	public static CardCollection getCardCollection(int id)
	{
		return collections.get(id);
	}
}
