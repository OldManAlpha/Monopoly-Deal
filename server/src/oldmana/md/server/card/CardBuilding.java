package oldmana.md.server.card;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardBuildingData;

public class CardBuilding extends Card
{
	private int tier;
	private int rentAddition;
	
	public CardBuilding(int value, String name, int tier, int rentAddition)
	{
		super(value, name);
		this.tier = tier;
		this.rentAddition = rentAddition;
		setRevocable(true);
		setClearsRevocableCards(false);
	}
	
	public int getTier()
	{
		return tier;
	}
	
	public int getRentAddition()
	{
		return rentAddition;
	}
	
	@Override
	public CardType getType()
	{
		return CardType.BUILDING;
	}
	
	@Override
	public Packet getCardDataPacket()
	{
		return new PacketCardBuildingData(getID(), getName(), getValue(), tier, rentAddition, isRevocable(), clearsRevocableCards(), getDisplayName(), 
				(byte) getFontSize(), (byte) getDisplayOffsetY(), getDescription().getID());
	}
}
