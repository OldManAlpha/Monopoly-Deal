package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPropertyColors extends Packet
{
	public String[] name;
	public String[] label;
	public int[] color;
	public boolean[] buildable;
	public byte[][] rents;
	
	public PacketPropertyColors() {}
}
