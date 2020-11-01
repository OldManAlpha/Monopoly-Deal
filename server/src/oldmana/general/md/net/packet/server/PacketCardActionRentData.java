package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardActionRentData extends Packet
{
	public int id;
	public byte value;
	public byte[] colors;
	
	public PacketCardActionRentData() {}
	
	public PacketCardActionRentData(int id, int value, byte[] colors)
	{
		this.id = id;
		this.value = (byte) value;
		this.colors = colors;
	}
}
