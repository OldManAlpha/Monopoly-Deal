package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayCardAction extends Packet
{
	public int id;
	
	public PacketActionPlayCardAction() {}
	
	public PacketActionPlayCardAction(int id)
	{
		this.id = id;
	}
}
