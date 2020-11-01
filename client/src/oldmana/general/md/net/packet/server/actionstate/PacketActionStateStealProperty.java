package oldmana.general.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateStealProperty extends Packet
{
	public int thief;
	public int card;
	
	public PacketActionStateStealProperty() {}
	
	public PacketActionStateStealProperty(int thief, int card)
	{
		this.thief = thief;
		this.card = card;
	}
}
