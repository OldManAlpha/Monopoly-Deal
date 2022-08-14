package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketDestroyButton extends Packet
{
	public int id;
	
	public PacketDestroyButton() {}

	public PacketDestroyButton(int id)
	{
		this.id = id;
	}
}
