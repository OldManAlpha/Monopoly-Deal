package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ActionStateTargetPlayerProperty extends ActionState
{
	private Card targetCard;
	
	public ActionStateTargetPlayerProperty(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Sly Deal"));
	}
	
	public void setTargetCard(Card card)
	{
		targetCard = card;
	}
	
	public Card getTargetCard()
	{
		return targetCard;
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}

	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.TARGET_PLAYER_PROPERTY, 0);
	}
	
}
