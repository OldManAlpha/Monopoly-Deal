package oldmana.md.net.packet.client.action;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketActionSelectCardCombo extends Packet
{
	public int selected;
	public int[] prevSelected;
	
	public PacketActionSelectCardCombo() {}
	
	public PacketActionSelectCardCombo(int selected, int[] prevSelected)
	{
		this.selected = selected;
		this.prevSelected = prevSelected;
	}
}
