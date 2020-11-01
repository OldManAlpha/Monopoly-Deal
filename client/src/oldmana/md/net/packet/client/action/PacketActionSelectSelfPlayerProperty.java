package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionSelectSelfPlayerProperty extends Packet
{
	public int selfCard;
	public int otherCard;
	
	public PacketActionSelectSelfPlayerProperty() {}
	
	public PacketActionSelectSelfPlayerProperty(int selfCard, int otherCard)
	{
		this.selfCard = selfCard;
		this.otherCard = otherCard;
	}
}
