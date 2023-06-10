package oldmana.md.common.net.packet.client.action;

import oldmana.md.common.net.api.packet.Packet;

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
