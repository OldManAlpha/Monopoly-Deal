package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketKick extends Packet
{
	public String reason;
	
	public PacketKick() {}
	
	public PacketKick(String reason)
	{
		this.reason = reason;
	}
}
