package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionButtonClick extends Packet
{
	public byte id;
	public int playerID;
	
	public PacketActionButtonClick() {}
	
	public PacketActionButtonClick(int id, int playerID)
	{
		this.id = (byte) id;
		this.playerID = playerID;
	}
}
