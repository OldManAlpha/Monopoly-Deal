package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketMovePropertySet extends Packet
{
	public int id;
	public int playerId;
	public int index;
	
	public PacketMovePropertySet() {}
	
	public PacketMovePropertySet(int id, int playerId, int index)
	{
		this.id = id;
		this.playerId = playerId;
		this.index = index;
	}
}
