package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionPay extends Packet
{
	public int[] ids;
	
	public PacketActionPay() {}
	
	public PacketActionPay(int[] ids)
	{
		this.ids = ids;
	}
}
