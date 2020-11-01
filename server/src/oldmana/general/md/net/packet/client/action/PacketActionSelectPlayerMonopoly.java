package oldmana.general.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionSelectPlayerMonopoly extends Packet
{
	public int id;
	
	public PacketActionSelectPlayerMonopoly() {}
	
	public PacketActionSelectPlayerMonopoly(int id)
	{
		this.id = id;
	}
}
