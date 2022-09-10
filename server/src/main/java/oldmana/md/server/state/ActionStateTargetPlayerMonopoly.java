package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.PropertySet;

public class ActionStateTargetPlayerMonopoly extends ActionState
{
	public ActionStateTargetPlayerMonopoly(Player player)
	{
		super(player);
		getServer().getGameState().setStatus(player.getName() + " used Deal Breaker");
	}
	
	public void onSetSelected(PropertySet set)
	{
		getActionOwner().clearRevocableCards();
		getGameState().setActionState(new ActionStateStealMonopoly(getActionOwner(), set));
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
