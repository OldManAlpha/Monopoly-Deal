package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketMoveCard extends Packet
{
	public int cardId;
	public int collectionId;
	public short index;
	public float time;
	public byte anim;
	
	public PacketMoveCard() {}
	
	/**
	 * 
	 * @param cardId - The card to move
	 * @param collectionId - The collection to move the card to
	 * @param index - The index the card should be placed at, -1 for append
	 */
	public PacketMoveCard(int cardId, int collectionId, int index, double time, int anim)
	{
		this.cardId = cardId;
		this.collectionId = collectionId;
		this.index = (short) index;
		this.time = (float) time;
		this.anim = (byte) anim;
	}
}
