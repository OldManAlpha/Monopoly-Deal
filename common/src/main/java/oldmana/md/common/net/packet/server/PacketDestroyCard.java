package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyCard extends Packet
{
	public int cardID;
	
	public PacketDestroyCard() {}
	
	public PacketDestroyCard(int cardID)
	{
		this.cardID = cardID;
	}
}
