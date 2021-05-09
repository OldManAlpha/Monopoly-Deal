package oldmana.md.server.card.collection;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardCollectionData;
import oldmana.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class Hand extends CardCollection
{
	public Hand(Player owner)
	{
		super(owner);
		getServer().broadcastPacket(getCollectionDataPacket(), getOwner());
		if (getOwner().isOnline())
		{
			getOwner().sendPacket(getOwnerHandDataPacket());
		}
	}
	
	public boolean hasTooManyCards()
	{
		return getCardCount() > 7;
	}
	
	@Override
	public void transferCard(Card card, CardCollection to, int index, double speed)
	{
		super.transferCard(card, to, index, speed);
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return player == getOwner();
	}
	
	public Packet getOwnerHandDataPacket()
	{
		return new PacketCardCollectionData(getID(), getOwner().getID(), getCardIds(), CardCollectionType.HAND);
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketUnknownCardCollectionData(getID(), getOwner().getID(), getCardCount(), CardCollectionType.HAND);
	}
}
