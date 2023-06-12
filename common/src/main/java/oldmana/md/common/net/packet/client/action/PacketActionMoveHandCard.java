package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionMoveHandCard extends Packet
{
	public int cardID;
	public int index;
	
	public PacketActionMoveHandCard() {}
	
	public PacketActionMoveHandCard(int cardID, int index)
	{
		this.cardID = cardID;
		this.index = index;
	}
}
