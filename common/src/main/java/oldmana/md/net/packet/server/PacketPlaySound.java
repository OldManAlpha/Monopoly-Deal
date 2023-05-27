package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPlaySound extends Packet
{
	public String name;
	public boolean queued;
	
	public PacketPlaySound() {}
	
	public PacketPlaySound(String name, boolean queued)
	{
		this.name = name;
		this.queued = queued;
	}
}
