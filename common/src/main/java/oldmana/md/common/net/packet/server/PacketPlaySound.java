package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

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
