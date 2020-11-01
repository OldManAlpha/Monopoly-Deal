package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateTradeProperties;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ActionStateTradeProperties extends ActionState
{
	private CardProperty ownerCard;
	private CardProperty targetCard;
	
	public ActionStateTradeProperties(CardProperty ownerCard, CardProperty targetCard)
	{
		super(ownerCard.getOwner(), targetCard.getOwner());
		this.ownerCard = ownerCard;
		this.targetCard = targetCard;
		getServer().broadcastPacket(new PacketStatus(getActionOwner().getName() + " used Forced Deal against " + getActionTarget().getTarget().getName()));
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
			getActionOwner().safelyGrantProperty(targetCard);
			player.safelyGrantProperty(ownerCard);
		}
		super.setAccepted(player, accepted);
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateTradeProperties(ownerCard.getID(), targetCard.getID());
	}
}
