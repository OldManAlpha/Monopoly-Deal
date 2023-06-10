package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionSelectPlayerMonopoly extends Packet
{
	public int id;
	
	public PacketActionSelectPlayerMonopoly() {}
	
	public PacketActionSelectPlayerMonopoly(int id)
	{
		this.id = id;
	}
}
