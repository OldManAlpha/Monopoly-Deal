package oldmana.general.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionStateTradeProperties extends Packet
{
	public int selfCard;
	public int otherCard;
	
	public PacketActionStateTradeProperties() {}
	
	public PacketActionStateTradeProperties(int selfCard, int otherCard)
	{
		this.selfCard = selfCard;
		this.otherCard = otherCard;
	}
}
