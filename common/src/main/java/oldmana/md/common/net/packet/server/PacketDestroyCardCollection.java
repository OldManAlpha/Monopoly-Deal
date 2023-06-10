package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyCardCollection extends Packet
{
	public int id;
	
	public PacketDestroyCardCollection() {}
	
	public PacketDestroyCardCollection(int id)
	{
		this.id = id;
	}
}
