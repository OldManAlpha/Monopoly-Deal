package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketDestroyCardCollection extends Packet
{
	public int id;
	
	public PacketDestroyCardCollection() {}
	
	public PacketDestroyCardCollection(int id)
	{
		this.id = id;
	}
}
