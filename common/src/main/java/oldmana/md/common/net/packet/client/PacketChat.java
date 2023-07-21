package oldmana.md.common.net.packet.client;

import oldmana.md.common.net.api.packet.Packet;

public class PacketChat extends Packet
{
	public String message;
	
	public PacketChat() {}
	
	public PacketChat(String message)
	{
		this.message = message;
	}
}
