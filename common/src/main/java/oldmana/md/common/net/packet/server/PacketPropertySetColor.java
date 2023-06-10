package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
