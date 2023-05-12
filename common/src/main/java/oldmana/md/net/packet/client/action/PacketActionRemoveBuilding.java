package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionRemoveBuilding extends Packet
{
	public int card;
	
	public PacketActionRemoveBuilding() {}
	
	public PacketActionRemoveBuilding(int card)
	{
		this.card = card;
	}
}
