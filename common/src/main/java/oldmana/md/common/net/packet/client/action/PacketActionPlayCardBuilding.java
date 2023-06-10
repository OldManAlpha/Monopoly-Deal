package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

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
