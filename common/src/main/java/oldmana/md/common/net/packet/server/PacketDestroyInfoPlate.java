package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyInfoPlate extends Packet
{
	public int player;
	public int id;
	
	public PacketDestroyInfoPlate() {}
	
	public PacketDestroyInfoPlate(int player, int id)
	{
		this.player = player;
		this.id = id;
	}
}
