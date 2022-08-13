package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStatePropertiesSelected;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public class ActionStateStealProperty extends ActionState
{
	private CardProperty targetCard;
	
	public ActionStateStealProperty(Player player, CardProperty targetCard)
	{
		super(player, targetCard.getOwner());
		this.targetCard = targetCard;
		getServer().getGameState().setStatus(player.getName() + " used Sly Deal against " + getActionTarget().getPlayer().getName());
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
			getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
			getActionOwner().safelyGrantProperty(targetCard);
		}
		super.setAccepted(player, accepted);
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStatePropertiesSelected(getActionOwner().getID(), getActionTarget().getPlayer().getID(), new int[] {targetCard.getID()});
	}
}
