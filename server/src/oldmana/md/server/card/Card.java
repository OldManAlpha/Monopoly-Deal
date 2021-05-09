package oldmana.md.server.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.net.packet.server.PacketCardDescription;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.CardCollection;

public class Card
{
	public static List<Card> cards = new ArrayList<Card>();
	
	private static int nextID;
	
	private static CardDescription defaultDescription = new CardDescription("Missing card description");
	
	
	private int id;
	
	private CardCollection collection;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize;
	private int displayOffsetY;
	private CardDescription description;
	
	private boolean revocable = true;
	private boolean clearsRevocableCards = false;
	
	public Card(int value, String name)
	{
		id = nextID++;
		registerCard(this);
		
		this.value = value;
		this.name = name;
		
		displayName = new String[] {name.toUpperCase()};
		fontSize = 8;
		displayOffsetY = 0;
		description = defaultDescription;
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
	
	public void setClearsRevocableCards(boolean clears)
	{
		clearsRevocableCards = clears;
	}
	
	public boolean clearsRevocableCards()
	{
		return clearsRevocableCards;
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
	
	/**Might be removed in the future in favor of setDescription(CardDescription)
	 * 
	 * @param description
	 */
	public void setDescription(String... description)
	{
		CardDescription desc = CardDescription.getDescriptionByText(description);
		if (desc == null)
		{
			desc = new CardDescription(description);
		}
		this.description = desc;
	}
	
	public void setDescription(CardDescription description)
	{
		this.description = description;
	}
	
	public CardDescription getDescription()
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
		return new PacketCardData(id, name, value, getType().getID(), revocable, clearsRevocableCards, displayName, (byte) fontSize, 
				(byte) displayOffsetY, description.getID());
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
	
	public static class CardDescription
	{
		private static int nextID;
		private static Map<Integer, CardDescription> descriptionMap = new HashMap<Integer, CardDescription>();
		private static Map<Integer, CardDescription> hashMap = new HashMap<Integer, CardDescription>();
		
		private int id;
		private String[] description;
		
		public CardDescription(String... description)
		{
			id = nextID++;
			this.description = description;
			descriptionMap.put(id, this);
			hashMap.put(Arrays.hashCode(description), this);
			MDServer.getInstance().broadcastPacket(new PacketCardDescription(id, description));
		}
		
		public int getID()
		{
			return id;
		}
		
		public String[] getText()
		{
			return description;
		}
		
		public static Collection<CardDescription> getAllDescriptions()
		{
			return descriptionMap.values();
		}
		
		public static CardDescription getDescriptionByID(int id)
		{
			return descriptionMap.get(id);
		}
		
		public static CardDescription getDescriptionByText(String[] text)
		{
			int hash = Arrays.hashCode(text);
			return hashMap.get(hash);
			/*
			MapIter:
			for (Entry<Integer, CardDescription> entry : descriptionMap.entrySet())
			{
				CardDescription desc = entry.getValue();
				String[] descText = desc.getText();
				if (descText.length == text.length)
				{
					for (int i = 0 ; i < text.length ; i++)
					{
						if (!descText[i].equals(text[i]))
						{
							continue MapIter;
						}
					}
					return desc;
				}
			}
			*/
		}
	}
	
	@Override
	public String toString()
	{
		return getName() + " (" + getValue() + "M)";
	}
	

	public static List<Card> getRegisteredCards()
	{
		return cards;
	}

	public static void registerCard(Card card)
	{
		cards.add(card);
	}
	
	public static void unregisterCard(Card card)
	{
		cards.remove(card);
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
