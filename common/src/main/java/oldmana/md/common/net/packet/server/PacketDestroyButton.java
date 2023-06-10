package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyButton extends Packet
{
	public int id;
	
	public PacketDestroyButton() {}

	public PacketDestroyButton(int id)
	{
		this.id = id;
	}
}
