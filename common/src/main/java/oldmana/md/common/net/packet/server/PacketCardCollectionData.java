package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketCardCollectionData extends Packet
{
	public int id;
	public int[] cardIds;
	public int owner;
	public byte type;
	
	public PacketCardCollectionData() {}
	
	public PacketCardCollectionData(int id, int owner, int[] cardIds, CardCollectionType type)
	{
		this.id = id;
		this.cardIds = cardIds;
		this.owner = owner;
		this.type = (byte) type.getID();
	}
	
	public PacketCardCollectionData(int id, int owner, CardCollectionType type)
	{
		this.id = id;
		this.cardIds = new int[] {};
		this.owner = owner;
		this.type = (byte) type.getID();
	}
	
	public enum CardCollectionType
	{
		DECK(0), DISCARD_PILE(1), BANK(2), HAND(3), VOID(4);
		
		private int id;
		
		CardCollectionType(int id)
		{
			this.id = id;
		}
		
		public int getID()
		{
			return id;
		}
		
		public static CardCollectionType fromID(int id)
		{
			for (CardCollectionType type : values())
			{
				if (type.getID() == id)
				{
					return type;
				}
			}
			return null;
		}
	}
}
