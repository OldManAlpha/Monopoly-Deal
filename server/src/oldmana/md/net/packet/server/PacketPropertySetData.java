package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketPropertySetData extends Packet
{
	public int id;
	public int[] cardIds;
	public int owner;
	public byte activeColor;
	
	public PacketPropertySetData() {}
	
	public PacketPropertySetData(int id, int owner, int[] cardIds, byte activeColor)
	{
		this.id = id;
		this.cardIds = cardIds;
		this.owner = owner;
		this.activeColor = activeColor;
	}
	
	public PacketPropertySetData(int id, int owner)
	{
		this.id = id;
		this.cardIds = new int[] {};
		this.owner = owner;
		this.activeColor = -1;
	}
}
