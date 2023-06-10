package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
