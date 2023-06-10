package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketRemoveMessageCategory extends Packet
{
	public String category;
	
	public PacketRemoveMessageCategory() {}
	
	public PacketRemoveMessageCategory(String category)
	{
		this.category = category;
	}
}
