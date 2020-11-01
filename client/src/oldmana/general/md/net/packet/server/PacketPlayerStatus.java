package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPlayerStatus extends Packet
{
	public int player;
	public boolean connected;
	
	public PacketPlayerStatus() {}
	
	public PacketPlayerStatus(int player, boolean connected)
	{
		this.player = player;
		this.connected = connected;
	}
}
