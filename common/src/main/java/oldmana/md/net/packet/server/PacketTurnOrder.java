package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketTurnOrder extends Packet
{
	public int[] order;
	
	public PacketTurnOrder() {}
	
	public PacketTurnOrder(int[] order)
	{
		this.order = order;
	}
}
