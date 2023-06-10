package oldmana.md.server.state;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

public abstract class ActionStateTargetPlayer extends ActionState
{
	public ActionStateTargetPlayer(Player player)
	{
		super(player);
	}
	
	public abstract void playerSelected(Player player);
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_PLAYER, 0);
	}
}
