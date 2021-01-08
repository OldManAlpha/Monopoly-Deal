package oldmana.md.net.packet.universal;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketChat extends Packet
{
	public String message;
	
	public PacketChat() {}
	
	public PacketChat(String message)
	{
		this.message = message;
	}
}
