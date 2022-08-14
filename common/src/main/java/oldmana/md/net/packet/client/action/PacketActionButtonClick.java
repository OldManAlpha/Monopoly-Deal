package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionButtonClick extends Packet
{
	public int id;
	
	public PacketActionButtonClick() {}
	
	public PacketActionButtonClick(int id)
	{
		this.id = id;
	}
}
