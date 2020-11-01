package oldmana.general.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionDiscard extends Packet
{
	public int card;
	
	public PacketActionDiscard() {}
	
	public PacketActionDiscard(int card)
	{
		this.card = card;
	}
}
