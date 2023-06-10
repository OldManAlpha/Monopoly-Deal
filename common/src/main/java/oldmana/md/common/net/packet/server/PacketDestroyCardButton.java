package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketDestroyCardButton extends Packet
{
	public int cardID;
	public byte pos;
	
	public PacketDestroyCardButton() {}

	public PacketDestroyCardButton(int cardID, int pos)
	{
		this.cardID = cardID;
		this.pos = (byte) pos;
	}
}
