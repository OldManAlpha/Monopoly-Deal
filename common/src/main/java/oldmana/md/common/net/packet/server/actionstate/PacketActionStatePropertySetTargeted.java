package oldmana.md.common.net.packet.server.actionstate;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionStatePropertySetTargeted extends Packet
{
	public int player;
	public int collection;
	
	public PacketActionStatePropertySetTargeted() {}
	
	public PacketActionStatePropertySetTargeted(int player, int collection)
	{
		this.player = player;
		this.collection = collection;
	}
}
