package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
