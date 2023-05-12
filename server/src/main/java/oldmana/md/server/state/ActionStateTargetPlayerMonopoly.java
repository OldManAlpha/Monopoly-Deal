package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.PropertySet;

public abstract class ActionStateTargetPlayerMonopoly extends ActionState
{
	public ActionStateTargetPlayerMonopoly(Player player)
	{
		super(player);
	}
	
	public abstract void onSetSelected(PropertySet set);
	
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
