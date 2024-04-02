package oldmana.md.server.state;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

/**
 * A base action state for where no typical in-game action is being asked of players.
 */
public class ActionStateIdle extends ActionState
{
	public ActionStateIdle()
	{
		super(null);
	}
	
	public ActionStateIdle(Player player)
	{
		super(player);
	}
	
	public ActionStateIdle(Player player, String status)
	{
		super(player, status);
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner() != null ? getActionOwner().getID() : -1,
				BasicActionState.DO_NOTHING, 0);
	}
}
