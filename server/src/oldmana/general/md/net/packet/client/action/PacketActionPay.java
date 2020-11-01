package oldmana.general.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPay extends Packet
{
	public int[] ids;
	
	public PacketActionPay() {}
	
	public PacketActionPay(int[] ids)
	{
		this.ids = ids;
	}
}
