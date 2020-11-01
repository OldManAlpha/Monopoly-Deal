package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.server.Player;

public class ActionStateTargetRent extends ActionStateTargetPlayer
{
	private int rent;
	
	public ActionStateTargetRent(Player player, int rent)
	{
		super(player);
		this.rent = rent;
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used a rent card"));
	}
	
	@Override
	public void playerSelected(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateRent(getActionOwner(), player, rent));
		getActionOwner().clearRevokableCards();
	}
}
