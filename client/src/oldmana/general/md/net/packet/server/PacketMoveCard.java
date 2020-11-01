package oldmana.general.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketMoveCard extends Packet
{
	public int cardId;
	public int collectionId;
	public short index;
	public float speed;
	
	public PacketMoveCard() {}
	
	/**
	 * 
	 * @param cardId - The card to move
	 * @param collectionId - The collection to move the card to
	 * @param index - The index the card should be placed at, -1 for append
	 */
	public PacketMoveCard(int cardId, int collectionId, int index, double speed)
	{
		this.cardId = cardId;
		this.collectionId = collectionId;
		this.index = (short) index;
		this.speed = (float) speed;
	}
}
