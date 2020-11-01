package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.server.Player;

public class ActionStateTargetDebtCollector extends ActionStateTargetPlayer
{
	public ActionStateTargetDebtCollector(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Debt Collector"));
	}
	
	@Override
	public void playerSelected(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateRent(getActionOwner(), player, 5));
		getActionOwner().clearRevokableCards();
	}
}
