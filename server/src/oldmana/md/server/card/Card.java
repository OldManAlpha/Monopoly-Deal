package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.action.CardActionDoubleTheRent;
import oldmana.md.server.card.action.CardActionJustSayNo;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.util.IDCounter;

public class Card
{
	private int id;
	
	private CardCollection collection;
	
	private int value;
	private String name;
	
	private String[] displayName;
	private int fontSize;
	private int displayOffsetY;
	
	private boolean revocable = true;
	private boolean marksPreviousCardsUnrevocable = false;
	
	public Card(int value, String name)
	{
		id = IDCounter.nextCardID();
		CardRegistry.registerCard(this);
		
		this.value = value;
		this.name = name;
		
		displayName = new String[] {name};
		fontSize = 8;
		displayOffsetY = 0;
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
	
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}
	
	public void setDisplayOffsetY(int offset)
	{
		this.displayOffsetY = offset;
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
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
	
	public Packet getCardDataPacket()
	{
		return new PacketCardData(id, name, value, CardType.getTypeOf(this).getID(), revocable, marksPreviousCardsUnrevocable, displayName, (byte) fontSize, 
				(byte) displayOffsetY);
	}
	
	public static enum CardType
	{
		MONEY(0), PROPERTY(1), ACTION(2), JUST_SAY_NO(3), DOUBLE_THE_RENT(4), SPECIAL(5);
		
		private int id;
		
		CardType(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
		
		public static CardType getTypeOf(Card card)
		{
			if (card instanceof CardProperty)
			{
				return PROPERTY;
			}
			else if (card instanceof CardActionJustSayNo)
			{
				return JUST_SAY_NO;
			}
			else if (card instanceof CardActionDoubleTheRent)
			{
				return DOUBLE_THE_RENT;
			}
			else if (card instanceof CardAction)
			{
				return ACTION;
			}
			else if (card instanceof CardSpecial)
			{
				return SPECIAL;
			}
			return MONEY;
		}
	}
	
	@Override
	public String toString()
	{
		return new String(getName() + " (" + getValue() + "M)");
	}
}
