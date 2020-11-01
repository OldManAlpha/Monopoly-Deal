package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
