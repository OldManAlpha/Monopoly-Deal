package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

public class PacketActionDiscard extends Packet
{
	public int card;
	
	public PacketActionDiscard() {}
	
	public PacketActionDiscard(int card)
	{
		this.card = card;
	}
}
