package oldmana.general.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateRent extends Packet
{
	public int renter;
	public int[] rented;
	public int amount;
	
	public PacketActionStateRent() {}
	
	public PacketActionStateRent(int renter, int[] rented, int amount)
	{
		this.renter = renter;
		this.rented = rented;
		this.amount = amount;
	}
}
