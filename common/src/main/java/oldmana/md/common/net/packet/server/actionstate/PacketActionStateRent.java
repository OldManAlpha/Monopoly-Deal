package oldmana.md.common.net.packet.server.actionstate;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionStateRent extends Packet
{
	public int renter;
	public int[] rented;
	public int[] amounts;
	
	public PacketActionStateRent() {}
	
	public PacketActionStateRent(int renter, int[] rented, int[] amounts)
	{
		this.renter = renter;
		this.rented = rented;
		this.amounts = amounts;
	}
}
