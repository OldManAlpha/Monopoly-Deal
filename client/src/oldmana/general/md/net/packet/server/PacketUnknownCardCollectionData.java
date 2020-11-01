package oldmana.general.md.net.packet.server;

import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketUnknownCardCollectionData extends Packet
{
	public int id;
	public int cardCount;
	public int owner;
	public byte type;
	
	public PacketUnknownCardCollectionData() {}
	
	public PacketUnknownCardCollectionData(int id, int owner, int cardCount, CardCollectionType type)
	{
		this.id = id;
		this.cardCount = cardCount;
		this.owner = owner;
		this.type = (byte) type.getID();
	}
	
	public PacketUnknownCardCollectionData(int id, int owner, CardCollectionType type)
	{
		this.id = id;
		this.cardCount = 0;
		this.owner = owner;
		this.type = (byte) type.getID();
	}
}
