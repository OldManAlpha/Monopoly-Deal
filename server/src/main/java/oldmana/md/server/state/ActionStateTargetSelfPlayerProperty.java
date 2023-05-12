package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public abstract class ActionStateTargetSelfPlayerProperty extends ActionState
{
	private boolean canTargetSelfMonopoly = true;
	private boolean canTargetOtherMonopoly = false;
	private boolean canTargetNonBase = false;
	
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
		int data = 0;
		if (canTargetSelfMonopoly)
		{
			data |= 1 << 0;
		}
		if (canTargetOtherMonopoly)
		{
			data |= 1 << 1;
		}
		if (canTargetNonBase)
		{
			data |= 1 << 2;
		}
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_SELF_PLAYER_PROPERTY, data);
	}
}
