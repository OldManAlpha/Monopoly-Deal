package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketRemoveMessageCategory extends Packet
{
	public String category;
	
	public PacketRemoveMessageCategory() {}
	
	public PacketRemoveMessageCategory(String category)
	{
		this.category = category;
	}
}
