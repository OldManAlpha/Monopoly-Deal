package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionSelectPlayer extends Packet
{
	public int player;
	
	public PacketActionSelectPlayer() {}
	
	public PacketActionSelectPlayer(int player)
	{
		this.player = player;
	}
}
