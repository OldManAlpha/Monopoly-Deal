package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyPlayer extends Packet
{
	public int player;
	
	public PacketDestroyPlayer() {}
	
	public PacketDestroyPlayer(int player)
	{
		this.player = player;
	}
}
