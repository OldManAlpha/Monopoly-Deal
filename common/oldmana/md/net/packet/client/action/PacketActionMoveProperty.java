package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
