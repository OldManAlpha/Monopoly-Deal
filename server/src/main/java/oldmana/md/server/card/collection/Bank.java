package oldmana.md.server.card.collection;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardCollectionData;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

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
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return true;
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketCardCollectionData(getID(), getOwner().getID(), getCardIDs(), CardCollectionType.BANK);
	}
}
