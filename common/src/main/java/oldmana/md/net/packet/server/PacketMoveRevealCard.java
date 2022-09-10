package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketMoveRevealCard extends Packet
{
	public int cardId;
	public int from;
	public int to;
	public short index;
	public float time;
	public byte anim;
	
	public PacketMoveRevealCard() {}
	
	public PacketMoveRevealCard(int cardId, int from, int to, int index, double time, int anim)
	{
		this.cardId = cardId;
		this.from = from;
		this.to = to;
		this.index = (short) index;
		this.time = (float) time;
		this.anim = (byte) anim;
	}
}
