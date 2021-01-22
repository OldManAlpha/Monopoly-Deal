package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardDescription extends Packet
{
	public short id;
	public String[] description;
	
	public PacketCardDescription() {}
	
	public PacketCardDescription(int id, String[] description)
	{
		this.id = (short) id;
		this.description = description;
	}
}
