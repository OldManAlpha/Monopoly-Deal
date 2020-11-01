package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.PacketStatus;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateStealProperty;
import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.Card;
import oldmana.general.md.server.card.CardProperty;
import oldmana.general.mjnetworkingapi.packet.Packet;

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
