package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayCardSpecial extends Packet
{
	public int id;
	public int data;
	
	public PacketActionPlayCardSpecial() {}
	
	public PacketActionPlayCardSpecial(int id, int data)
	{
		this.id = id;
		this.data = data;
	}
}
