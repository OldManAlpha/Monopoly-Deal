package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayMultiCardAction extends Packet
{
	public int[] ids;
	
	public PacketActionPlayMultiCardAction() {}
	
	public PacketActionPlayMultiCardAction(int[] ids)
	{
		this.ids = ids;
	}
}
