package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateStealProperty;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;

public class ActionStateStealProperty extends ActionState
{
	private CardProperty targetCard;
	
	public ActionStateStealProperty(Player player, CardProperty targetCard)
	{
		super(player, targetCard.getOwner());
		this.targetCard = targetCard;
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Sly Deal against " + getActionTarget().getTarget().getName()));
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
			getActionOwner().safelyGrantProperty(targetCard);
		}
		super.setAccepted(player, accepted);
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateStealProperty(getActionOwner().getID(), targetCard.getID());
	}
}
