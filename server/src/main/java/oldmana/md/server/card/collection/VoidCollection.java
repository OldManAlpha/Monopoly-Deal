package oldmana.md.server.card.collection;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.common.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;

public class VoidCollection extends CardCollection
{
	public VoidCollection()
	{
		super(null);
	}

	@Override
	public boolean isVisibleTo(Player player)
	{
		return false;
	}

	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketUnknownCardCollectionData(getID(), -1, CardCollectionType.VOID);
	}
	
}
