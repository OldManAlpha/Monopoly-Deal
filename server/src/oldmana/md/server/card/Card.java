package oldmana.md.server.card;

import java.util.ArrayList;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.util.IDCounter;

public class Card
{
	public static List<Card> cards = new ArrayList<Card>();
	
	
	private int id;
	
	private CardCollection collection;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize;
	private int displayOffsetY;
	private String[] description;
	
	private boolean revocable = true;
	private boolean marksPreviousCardsUnrevocable = false;
	
	public Card(int value, String name)
	{
		id = IDCounter.nextCardID();
		Card.registerCard(this);
		
		this.value = value;
		this.name = name;
		
		displayName = new String[] {name};
		fontSize = 8;
		displayOffsetY = 0;
		description = new String[] {"Missing card description"};
	}
	
	public int getID()
	{
		return id;
	}
	
	public Player getOwner()
	{
		return collection.getOwner();
	}
	
	public boolean hasOwner()
	{
		return collection.getOwner() != null;
	}
	
	public void setOwningCollection(CardCollection collection)
	{
		this.collection = collection;
	}
	
	public CardCollection getOwningCollection()
	{
		return collection;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setRevocable(boolean revocable)
	{
		this.revocable = revocable;
	}
	
	public boolean isRevocable()
	{
		return revocable;
	}
	
	public void setMarksPreviousUnrevocable(boolean marks)
	{
		marksPreviousCardsUnrevocable = marks;
	}
	
	public boolean marksPreviousUnrevocable()
	{
		return marksPreviousCardsUnrevocable;
	}
	
	public void setDisplayName(String... displayName)
	{
		this.displayName = displayName;
	}
	
	public String[] getDisplayName()
	{
		return displayName;
	}
	
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}
	
	public void setDisplayOffsetY(int offset)
	{
		this.displayOffsetY = offset;
	}
	
	public void setDescription(String... description)
	{
		this.description = description;
	}
	
	public String[] getDescription()
	{
		return description;
	}
	
	public void transfer(CardCollection to)
	{
		transfer(to, -1);
	}
	
	public void transfer(CardCollection to, int index)
	{
		transfer(to, index, 1);
	}
	
	public void transfer(CardCollection to, int index, double speed)
	{
		if (collection != null)
		{
			collection.transferCard(this, to, index, speed);
		}
	}
	
	public CardType getType()
	{
		return CardType.MONEY;
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public Packet getCardDataPacket()
	{
		return new PacketCardData(id, name, value, getType().getID(), revocable, marksPreviousCardsUnrevocable, displayName, (byte) fontSize, 
				(byte) displayOffsetY, description);
	}
	
	public static enum CardType
	{
		MONEY(0), PROPERTY(1), ACTION(2), JUST_SAY_NO(3), DOUBLE_THE_RENT(4), SPECIAL(5), RENT_COUNTER(6);
		
		private int id;
		
		CardType(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
	}
	
	@Override
	public String toString()
	{
		return new String(getName() + " (" + getValue() + "M)");
	}
	

	public static List<Card> getRegisteredCards()
	{
		return cards;
	}

	public static void registerCard(Card card)
	{
		cards.add(card);
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
}
