package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketCardActionRentData extends Packet
{
	public int id;
	public String name;
	public byte value;
	public byte[] colors;
	
	public short description;
	
	public PacketCardActionRentData() {}
	
	public PacketCardActionRentData(int id, String name, int value, byte[] colors, int description)
	{
		this.id = id;
		this.name = name;
		this.value = (byte) value;
		this.colors = colors;
		
		this.description = (short) description;
	}
}
