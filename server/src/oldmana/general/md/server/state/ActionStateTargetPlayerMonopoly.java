package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.general.md.server.Player;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ActionStateTargetPlayerMonopoly extends ActionState
{
	public ActionStateTargetPlayerMonopoly(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Deal Breaker"));
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}

	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_PLAYER_MONOPOLY, 0);
	}
	
}
