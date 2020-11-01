package oldmana.general.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayCardProperty extends Packet
{
	public int id;
	public int setId;
	
	public PacketActionPlayCardProperty() {}
	
	public PacketActionPlayCardProperty(int id, int setId)
	{
		this.id = id;
		this.setId = setId;
	}
}
