package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionSelectProperties extends Packet
{
	public int[] ids;
	
	public PacketActionSelectProperties() {}
	
	public PacketActionSelectProperties(int[] ids)
	{
		this.ids = ids;
	}
	
	public PacketActionSelectProperties(int id)
	{
		this.ids = new int[] {id};
	}
}
