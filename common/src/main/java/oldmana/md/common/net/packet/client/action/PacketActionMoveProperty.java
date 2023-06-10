package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionMoveProperty extends Packet
{
	public int id;
	public int setId;
	
	public PacketActionMoveProperty() {}
	
	public PacketActionMoveProperty(int id, int setId)
	{
		this.id = id;
		this.setId = setId;
	}
}
