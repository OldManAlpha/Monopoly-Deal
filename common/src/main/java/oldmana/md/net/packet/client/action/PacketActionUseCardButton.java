package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionUseCardButton extends Packet
{
	public int cardID;
	public byte pos;
	public int data;
	
	public PacketActionUseCardButton() {}

	public PacketActionUseCardButton(int cardID, int pos, int data)
	{
		this.cardID = cardID;
		this.pos = (byte) pos;
		this.data = data;
	}
}
