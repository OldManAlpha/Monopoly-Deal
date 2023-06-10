package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
