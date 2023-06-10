package oldmana.md.common.net.packet.server.actionstate;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.state.TargetState;

public class PacketUpdateActionStateTarget extends Packet
{
	public int target;
	public byte state;
	
	public PacketUpdateActionStateTarget() {}
	
	public PacketUpdateActionStateTarget(int target, TargetState state)
	{
		this.target = target;
		this.state = (byte) state.ordinal();
	}
	
	public TargetState getTargetState()
	{
		return TargetState.fromID(state);
	}
}
