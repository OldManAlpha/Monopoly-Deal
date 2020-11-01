package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionChangeSetColor extends Packet
{
	public int setId;
	public byte color;
	
	public PacketActionChangeSetColor() {}
	
	public PacketActionChangeSetColor(int setId, byte color)
	{
		this.setId = setId;
		this.color = color;
	}
}
