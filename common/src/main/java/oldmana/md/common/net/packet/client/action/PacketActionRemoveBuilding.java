package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionRemoveBuilding extends Packet
{
	public int card;
	
	public PacketActionRemoveBuilding() {}
	
	public PacketActionRemoveBuilding(int card)
	{
		this.card = card;
	}
}
