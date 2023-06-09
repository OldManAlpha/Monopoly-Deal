package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionUseCardButton extends Packet
{
	public int cardID;
	public byte id;
	public int data;
	
	public PacketActionUseCardButton() {}

	public PacketActionUseCardButton(int cardID, int id, int data)
	{
		this.cardID = cardID;
		this.id = (byte) id;
		this.data = data;
	}
}
