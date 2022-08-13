package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketKick extends Packet
{
	public String reason;
	
	public PacketKick() {}
	
	public PacketKick(String reason)
	{
		this.reason = reason;
	}
}
