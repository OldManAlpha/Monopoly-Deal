package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketStatus extends Packet
{
	public String text;
	
	public PacketStatus() {}
	
	public PacketStatus(String text)
	{
		this.text = text;
	}
}
