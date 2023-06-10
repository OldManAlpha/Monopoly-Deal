package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketSetChatOpen extends Packet
{
	public boolean open;
	
	public PacketSetChatOpen() {}
	
	public PacketSetChatOpen(boolean open)
	{
		this.open = open;
	}
}
