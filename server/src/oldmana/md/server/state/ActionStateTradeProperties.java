package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePropertiesSelected;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public class ActionStateTradeProperties extends ActionState
{
	private CardProperty ownerCard;
	private CardProperty targetCard;
	
	public ActionStateTradeProperties(CardProperty ownerCard, CardProperty targetCard)
	{
		super(ownerCard.getOwner(), targetCard.getOwner());
		this.ownerCard = ownerCard;
		this.targetCard = targetCard;
		getServer().getGameState().setStatus(getActionOwner().getName() + " used Forced Deal against " + getTargetPlayer().getName());
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
			getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
			getActionOwner().safelyGrantProperty(targetCard);
			player.safelyGrantProperty(ownerCard);
		}
		super.setAccepted(player, accepted);
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStatePropertiesSelected(getActionOwner().getID(), getTargetPlayer().getID(), new int[] {ownerCard.getID(), targetCard.getID()});
	}
}
