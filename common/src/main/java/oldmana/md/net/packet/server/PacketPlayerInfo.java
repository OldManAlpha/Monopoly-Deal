package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPlayerInfo extends Packet
{
	public int id;
	public String name;
	public boolean connected;
	
	public PacketPlayerInfo() {}
	
	public PacketPlayerInfo(int id, String name, boolean connected)
	{
		this.id = id;
		this.name = name;
		this.connected = connected;
	}
}
