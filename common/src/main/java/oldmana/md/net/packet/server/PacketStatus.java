package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketStatus extends Packet
{
	public String text;
	
	public PacketStatus() {}
	
	public PacketStatus(String text)
	{
		this.text = text;
	}
}
