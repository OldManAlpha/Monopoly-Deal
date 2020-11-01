package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketDestroyPlayer extends Packet
{
	public int player;
	
	public PacketDestroyPlayer() {}
	
	public PacketDestroyPlayer(int player)
	{
		this.player = player;
	}
}
