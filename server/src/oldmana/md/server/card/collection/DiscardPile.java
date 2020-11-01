package oldmana.md.server.card.collection;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardCollectionData;
import oldmana.md.net.packet.server.PacketMoveRevealCard;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class DiscardPile extends CardCollection
{
	public DiscardPile()
	{
		super(null);
	}
	
	/*
	@Override
	public void transferCard(Card card, CardCollection to)
	{
		super.transferCard(card, to);
		
		PacketMoveRevealCard packet = new PacketMoveRevealCard(card.getID(), getID(), to.getID(), to.getIndexOf(card));
		MDServer.getInstance().broadcastPacket(packet);
	}
	*/
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return true;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketCardCollectionData(getID(), -1, getCardIds(), CardCollectionType.DISCARD_PILE);
	}
}
