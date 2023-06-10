package oldmana.md.server.state;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

public class ActionStatePlayerTargeted extends ActionState
{
	public ActionStatePlayerTargeted(Player actionOwner, Player actionTarget)
	{
		super(actionOwner, actionTarget);
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.PLAYER_TARGETED, getTargetPlayer().getID());
	}
}
