package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public abstract class ActionStateTargetSelfPlayerProperty extends ActionState
{
	public ActionStateTargetSelfPlayerProperty(Player player)
	{
		super(player);
	}
	
	public abstract void onCardsSelected(CardProperty self, CardProperty other);
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_SELF_PLAYER_PROPERTY, 0);
	}
}
