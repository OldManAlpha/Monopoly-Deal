package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionClickLink extends Packet
{
	public int id;
	
	public PacketActionClickLink() {}
	
	public PacketActionClickLink(int id)
	{
		this.id = id;
	}
}
