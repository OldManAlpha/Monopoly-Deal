package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.action.CardActionForcedDeal;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ActionStateTargetSelfPlayerProperty extends ActionState
{
	private CardActionForcedDeal card;
	
	public ActionStateTargetSelfPlayerProperty(Player player, CardActionForcedDeal card)
	{
		super(player);
		this.card = card;
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Forced Deal"));
	}
	
	@Override
	public void onCardUndo(Card card)
	{
		super.onCardUndo(card);
	}
	
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
