package oldmana.md.net.packet.server.actionstate;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class PacketUpdateActionStateTarget extends Packet
{
	public int target;
	public boolean isTarget;
	
	public PacketUpdateActionStateTarget() {}
	
	public PacketUpdateActionStateTarget(int target, boolean isTarget)
	{
		this.target = target;
		this.isTarget = isTarget;
	}
}
