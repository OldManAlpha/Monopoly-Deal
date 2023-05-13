package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketSetChatOpen extends Packet
{
	public boolean open;
	
	public PacketSetChatOpen() {}
	
	public PacketSetChatOpen(boolean open)
	{
		this.open = open;
	}
}
