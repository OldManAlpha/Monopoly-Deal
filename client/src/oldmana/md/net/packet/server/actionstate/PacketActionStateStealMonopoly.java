package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateStealMonopoly extends Packet
{
	public int thief;
	public int collection;
	
	public PacketActionStateStealMonopoly() {}
	
	public PacketActionStateStealMonopoly(int thief, int collection)
	{
		this.thief = thief;
		this.collection = collection;
	}
}
