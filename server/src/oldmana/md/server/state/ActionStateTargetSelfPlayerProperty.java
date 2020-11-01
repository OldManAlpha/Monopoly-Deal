package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.action.CardActionForcedDeal;

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
