package oldmana.md.net.packet.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

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
