package oldmana.md.net.packet.client;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketQuit extends Packet
{
	public String reason = "";
	
	public PacketQuit() {}
	
	public PacketQuit(String reason)
	{
		this.reason = reason;
	}
}
