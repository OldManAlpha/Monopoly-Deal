package oldmana.md.common.net.packet.client;

import oldmana.md.common.net.api.packet.Packet;

public class PacketQuit extends Packet
{
	public String reason = "";
	
	public PacketQuit() {}
	
	public PacketQuit(String reason)
	{
		this.reason = reason;
	}
}
