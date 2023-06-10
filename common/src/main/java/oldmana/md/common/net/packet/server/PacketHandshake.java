package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketHandshake extends Packet
{
	public int id;
	public String name;
	
	public PacketHandshake() {}
	
	public PacketHandshake(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
}
