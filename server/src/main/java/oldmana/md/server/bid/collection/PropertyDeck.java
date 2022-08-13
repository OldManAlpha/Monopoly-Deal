package oldmana.md.server.bid.collection;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.CardCollection;

public class PropertyDeck extends CardCollection
{
	public PropertyDeck()
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
		// TODO Auto-generated method stub
		return null;
	}
}
