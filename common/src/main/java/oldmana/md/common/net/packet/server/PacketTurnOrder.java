package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketTurnOrder extends Packet
{
	public int[] order;
	
	public PacketTurnOrder() {}
	
	public PacketTurnOrder(int[] order)
	{
		this.order = order;
	}
}
