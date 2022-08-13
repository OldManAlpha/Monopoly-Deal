package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPlaySound extends Packet
{
	public String name;
	
	public PacketPlaySound() {}
	
	public PacketPlaySound(String name)
	{
		this.name = name;
	}
}
