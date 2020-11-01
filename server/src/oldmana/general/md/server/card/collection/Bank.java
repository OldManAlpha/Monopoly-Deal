package oldmana.general.md.server.card.collection;

import oldmana.general.md.net.packet.server.PacketCardCollectionData;
import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.md.net.packet.server.PacketMoveCard;
import oldmana.general.md.server.MDServer;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class Bank extends CardCollection
{
	public Bank(Player owner)
	{
		super(owner);
		getServer().broadcastPacket(getCollectionDataPacket());
	}
	
	public int getTotalValue()
	{
		int value = 0;
		for (Card card : getCards())
		{
			value += card.getValue();
		}
		return value;
	}
	
	/*
	@Override
	public void transferCard(Card card, CardCollection to)
	{
		super.transferCard(card, to);
		
		PacketMoveCard packet = new PacketMoveCard(card.getID(), to.getID(), -1);
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
		return new PacketCardCollectionData(getID(), getOwner().getID(), getCardIds(), CardCollectionType.BANK);
	}
}
