package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

public class ActionStateTargetPlayer extends ActionState
{
	public ActionStateTargetPlayer(Player player)
	{
		super(player);
	}
	
	public void playerSelected(Player player)
	{
		((TargetPlayerListener) getListener()).playerSelected(getActionOwner(), player);
	}
	
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
	
	public static interface TargetPlayerListener extends ActionStateListener
	{
		public void playerSelected(Player player, Player target);
	}
}
