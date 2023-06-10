package oldmana.md.server.card;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketCardData;
import oldmana.md.server.Player;

public class CardAction extends Card
{
	@Override
	public boolean canBank(Player player)
	{
		return super.canBank(player) && getServer().getGameRules().canBankActionCards();
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardData(getID(), getName(), getValue(), 0,
				getDisplayName(), (byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID(),
				getOuterColor().getRGB(), getInnerColor().getRGB());
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
