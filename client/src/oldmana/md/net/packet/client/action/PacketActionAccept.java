package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionAccept extends Packet
{
	public int playerId;
	
	public PacketActionAccept() {}
	
	public PacketActionAccept(int playerId)
	{
		this.playerId = playerId;
	}
}
