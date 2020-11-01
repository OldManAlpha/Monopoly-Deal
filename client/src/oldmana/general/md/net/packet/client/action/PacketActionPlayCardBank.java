package oldmana.general.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayCardBank extends Packet
{
	public int id;
	
	public PacketActionPlayCardBank() {}
	
	public PacketActionPlayCardBank(int id)
	{
		this.id = id;
	}
}
