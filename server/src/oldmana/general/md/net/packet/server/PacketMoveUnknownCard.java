package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketMoveUnknownCard extends Packet
{
	public int from;
	public int to;
	public float speed;
	
	public PacketMoveUnknownCard() {}
	
	public PacketMoveUnknownCard(int from, int to, double speed)
	{
		this.from = from;
		this.to = to;
		this.speed = (float) speed;
	}
}
