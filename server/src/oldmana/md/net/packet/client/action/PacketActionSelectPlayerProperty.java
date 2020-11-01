package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionSelectPlayerProperty extends Packet
{
	public int id;
	
	public PacketActionSelectPlayerProperty() {}
	
	public PacketActionSelectPlayerProperty(int id)
	{
		this.id = id;
	}
}
