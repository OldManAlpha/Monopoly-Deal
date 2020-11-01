package oldmana.general.md.server.card.collection;

import oldmana.general.md.net.packet.server.PacketCardCollectionData;
import oldmana.general.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class Hand extends CardCollection
{
	public Hand(Player owner)
	{
		super(owner);
		getServer().broadcastPacket(getCollectionDataPacket(), getOwner());
		if (getOwner().isLoggedIn())
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
	
	/*
	@Override
	public void transferCard(Card card, CardCollection to)
	{
		super.transferCard(card, to);
		PacketMoveRevealCard packet = new PacketMoveRevealCard(card.getID(), getID(), to.getID(), to.getIndexOf(card));
		getServer().broadcastPacket(packet, card.getOwner());
		PacketMoveCard packet2 = new PacketMoveCard(card.getID(), to.getID(), to.getIndexOf(card));
		card.getOwner().sendPacket(packet2);
	}
	*/
	
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
