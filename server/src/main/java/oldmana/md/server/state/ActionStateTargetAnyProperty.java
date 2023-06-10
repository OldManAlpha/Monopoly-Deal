package oldmana.md.server.state;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.util.DataUtil;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.common.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public abstract class ActionStateTargetAnyProperty extends ActionState
{
	private boolean canTargetSelfMonopoly = true;
	private boolean canTargetOtherMonopoly = true;
	private boolean canTargetNonBase = true;
	
	public ActionStateTargetAnyProperty(Player player)
	{
		super(player);
	}
	
	public abstract void onCardSelected(CardProperty card);
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_ANY_PROPERTY,
				DataUtil.convertBooleansToByte(canTargetSelfMonopoly, canTargetOtherMonopoly, canTargetNonBase));
	}
}
