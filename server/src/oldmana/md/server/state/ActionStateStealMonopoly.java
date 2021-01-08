package oldmana.md.server.state;

import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.collection.PropertySet;

public class ActionStateStealMonopoly extends ActionStatePropertySetTargeted
{
	private PropertySet targetSet;
	
	public ActionStateStealMonopoly(Player player, PropertySet targetSet)
	{
		super(player, targetSet);
		this.targetSet = targetSet;
		getServer().getGameState().setStatus(player.getName() + " used Deal Breaker against " + getActionTarget().getPlayer().getName());
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
			getServer().broadcastPacket(new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0)); // Bandaid Fix For Glitched Cards
			if (getServer().getGameRules().doDealBreakersDiscardSets())
			{
				for (CardProperty prop : targetSet.getPropertyCards())
				{
					prop.transfer(getServer().getDiscardPile());
				}
			}
			else
			{
				for (CardProperty prop : targetSet.getPropertyCards())
				{
					getActionOwner().safelyGrantProperty(prop);
				}
			}
		}
		super.setAccepted(player, accepted);
	}
}
