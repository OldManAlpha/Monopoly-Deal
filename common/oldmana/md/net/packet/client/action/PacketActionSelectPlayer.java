package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionSelectPlayer extends Packet
{
	public int player;
	
	public PacketActionSelectPlayer() {}
	
	public PacketActionSelectPlayer(int player)
	{
		this.player = player;
	}
}
