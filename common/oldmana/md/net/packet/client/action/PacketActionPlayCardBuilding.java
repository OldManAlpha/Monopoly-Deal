package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionPlayCardBuilding extends Packet
{
	public int id;
	public int setID;
	
	public PacketActionPlayCardBuilding() {}
	
	public PacketActionPlayCardBuilding(int id, int setID)
	{
		this.id = id;
		this.setID = setID;
	}
}
