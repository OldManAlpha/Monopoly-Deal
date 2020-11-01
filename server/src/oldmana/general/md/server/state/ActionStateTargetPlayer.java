package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.general.md.server.Player;
import oldmana.general.mjnetworkingapi.packet.Packet;

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
