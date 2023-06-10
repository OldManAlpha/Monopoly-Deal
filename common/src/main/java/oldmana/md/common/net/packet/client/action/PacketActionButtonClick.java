package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionButtonClick extends Packet
{
	public int id;
	
	public PacketActionButtonClick() {}
	
	public PacketActionButtonClick(int id)
	{
		this.id = id;
	}
}
