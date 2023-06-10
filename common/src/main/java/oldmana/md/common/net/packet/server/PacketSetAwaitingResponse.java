package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketSetAwaitingResponse extends Packet
{
	public boolean awaiting;
	
	public PacketSetAwaitingResponse() {}
	
	public PacketSetAwaitingResponse(boolean awaiting)
	{
		this.awaiting = awaiting;
	}
}
