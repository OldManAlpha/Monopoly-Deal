package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
