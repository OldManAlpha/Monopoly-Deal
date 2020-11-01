package oldmana.general.md.server.card.collection;

import oldmana.general.md.net.packet.server.PacketCardCollectionData;
import oldmana.general.md.net.packet.server.PacketMoveRevealCard;
import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.md.server.MDServer;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.mjnetworkingapi.packet.Packet;

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
