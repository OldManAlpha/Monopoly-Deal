package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketUpdatePlayer extends Packet
{
	public int player;
	public String name;
	public boolean connected;
	
	public PacketUpdatePlayer() {}
	
	public PacketUpdatePlayer(int player, String name, boolean connected)
	{
		this.player = player;
		this.name = name;
		this.connected = connected;
	}
}
