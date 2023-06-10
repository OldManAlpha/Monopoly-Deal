package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionClickLink extends Packet
{
	public int id;
	
	public PacketActionClickLink() {}
	
	public PacketActionClickLink(int id)
	{
		this.id = id;
	}
}
