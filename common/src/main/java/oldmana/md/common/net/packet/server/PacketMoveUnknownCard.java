package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketMoveUnknownCard extends Packet
{
	public int from;
	public int to;
	public float time;
	public byte anim;
	
	public PacketMoveUnknownCard() {}
	
	public PacketMoveUnknownCard(int from, int to, double time, int anim)
	{
		this.from = from;
		this.to = to;
		this.time = (float) time;
		this.anim = (byte) anim;
	}
}
