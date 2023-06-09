package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardData;
import oldmana.md.server.Player;

public class CardAction extends Card
{
	@Override
	public boolean canBank(Player player)
	{
		return getServer().getGameRules().canBankActionCards();
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardData(getID(), getName(), getValue(), 0, isUndoable(), shouldClearUndoableCards(),
				getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID());
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (" + getValue() + "M)";
	}
	
	private static CardType<CardAction> createType()
	{
		return new CardType<CardAction>(CardAction.class, "Action Card");
	}
}
