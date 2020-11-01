package oldmana.general.md.server.card.collection;

import oldmana.general.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.general.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.general.md.server.Player;
import oldmana.general.mjnetworkingapi.packet.Packet;

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
