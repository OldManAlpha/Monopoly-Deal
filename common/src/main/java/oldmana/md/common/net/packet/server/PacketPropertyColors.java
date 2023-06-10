package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketPropertyColors extends Packet
{
	public String[] name;
	public String[] label;
	public int[] color;
	public boolean[] buildable;
	public byte[][] rents;
	
	public PacketPropertyColors() {}
}
