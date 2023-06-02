package oldmana.md.server.card.collection;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardCollectionData;
import oldmana.md.net.packet.server.PacketUnknownCardCollectionData;
import oldmana.md.net.packet.server.PacketCardCollectionData.CardCollectionType;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.common.card.CardAnimationType;
import oldmana.md.server.card.CardProperty;

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
		return getCardCount() > getServer().getGameRules().getMaxCardsInHand();
	}
	
	public boolean hasAllProperties()
	{
		for (Card card : this)
		{
			if (!(card instanceof CardProperty))
			{
				return false;
			}
		}
		return getCardCount() > 0;
	}
	
	public void updateCardButtons()
	{
		for (Card card : this)
		{
			card.updateButtons();
		}
	}
	
	public void resendCardButtons()
	{
		for (Card card : this)
		{
			card.getControls().resendButtons();
		}
	}
	
	@Override
	public void transferCard(Card card, CardCollection to, int index, double time, CardAnimationType anim, boolean flash)
	{
		super.transferCard(card, to, index, time, anim, flash);
		card.getControls().resetButtons();
	}
	
	@Override
	public boolean isVisibleTo(Player player)
	{
		return player == getOwner();
	}
	
	public Packet getOwnerHandDataPacket()
	{
		return new PacketCardCollectionData(getID(), getOwner().getID(), getCardIDs(), CardCollectionType.HAND);
	}
	
	@Override
	public Packet getCollectionDataPacket()
	{
		return new PacketUnknownCardCollectionData(getID(), getOwner().getID(), getCardCount(), CardCollectionType.HAND);
	}
}
