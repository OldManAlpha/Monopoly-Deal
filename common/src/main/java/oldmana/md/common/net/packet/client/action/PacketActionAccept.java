package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionAccept extends Packet
{
	public int playerId;
	
	public PacketActionAccept() {}
	
	public PacketActionAccept(int playerId)
	{
		this.playerId = playerId;
	}
}
