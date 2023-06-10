package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketCardButton extends Packet
{
	public int cardID;
	public String text;
	public byte pos;
	public byte type;
	public byte color;
	
	public PacketCardButton() {}

	public PacketCardButton(int cardID, String text, int pos, int type, int color)
	{
		this.cardID = cardID;
		this.pos = (byte) pos;
		this.type = (byte) type;
		this.text = text;
		this.color = (byte) color;
	}
}
