package oldmana.md.server.card.collection;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketCardCollectionData;
import oldmana.md.common.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;

public class DiscardPile extends CardCollection
{
	public DiscardPile()
	{
		super(null);
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return true;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketCardCollectionData(getID(), -1, getCardIDs(), CardCollectionType.DISCARD_PILE);
	}
}
