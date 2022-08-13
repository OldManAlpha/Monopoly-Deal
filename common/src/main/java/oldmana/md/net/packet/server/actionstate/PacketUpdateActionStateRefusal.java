package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketUpdateActionStateRefusal extends Packet
{
	public int target;
	public boolean refused;
	
	public PacketUpdateActionStateRefusal() {}
	
	public PacketUpdateActionStateRefusal(int target, boolean refused)
	{
		this.target = target;
		this.refused = refused;
	}
}
