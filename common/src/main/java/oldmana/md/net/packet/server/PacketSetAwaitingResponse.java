package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketSetAwaitingResponse extends Packet
{
	public boolean awaiting;
	
	public PacketSetAwaitingResponse() {}
	
	public PacketSetAwaitingResponse(boolean awaiting)
	{
		this.awaiting = awaiting;
	}
}
