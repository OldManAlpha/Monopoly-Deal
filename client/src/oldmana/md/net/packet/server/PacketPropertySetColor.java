package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPropertySetColor extends Packet
{
	public int id;
	public byte color;
	
	public PacketPropertySetColor() {}
	
	public PacketPropertySetColor(int id, int color)
	{
		this.id = id;
		this.color = (byte) color;
	}
}
