package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStatePropertiesSelected extends Packet
{
	public int owner;
	public int target;
	public int[] cards;
	
	public PacketActionStatePropertiesSelected() {}
	
	public PacketActionStatePropertiesSelected(int owner, int target, int[] cards)
	{
		this.owner = owner;
		this.target = target;
		this.cards = cards;
	}
}
