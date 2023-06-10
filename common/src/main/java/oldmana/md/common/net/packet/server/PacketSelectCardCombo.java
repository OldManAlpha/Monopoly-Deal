package oldmana.md.common.net.packet.server;

import oldmana.md.common.net.api.packet.Packet;

public class PacketSelectCardCombo extends Packet
{
	public int[] selectedCards;
	public int[] availableCards;
	
	public PacketSelectCardCombo() {}
	
	public PacketSelectCardCombo(int[] selectedCards, int[] availableCards)
	{
		this.selectedCards = selectedCards;
		this.availableCards = availableCards;
	}
}
