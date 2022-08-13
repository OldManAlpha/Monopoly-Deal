package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
