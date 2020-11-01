package oldmana.general.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketUpdateActionStateAccepted extends Packet
{
	public int target;
	public boolean accepted;
	
	public PacketUpdateActionStateAccepted() {}
	
	public PacketUpdateActionStateAccepted(int target, boolean accepted)
	{
		this.target = target;
		this.accepted = accepted;
	}
}
