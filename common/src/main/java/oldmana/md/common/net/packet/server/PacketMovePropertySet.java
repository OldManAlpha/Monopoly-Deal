package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
