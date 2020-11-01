package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateStealMonopoly;
import oldmana.md.net.packet.server.actionstate.PacketActionStateStealProperty;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.collection.PropertySet;

public class ActionStateStealMonopoly extends ActionState
{
	private PropertySet targetSet;
	
	public ActionStateStealMonopoly(Player player, PropertySet targetSet)
	{
		super(player, targetSet.getOwner());
		this.targetSet = targetSet;
		getServer().broadcastPacket(new PacketStatus(player.getName() + " used Deal Breaker against " + getActionTarget().getTarget().getName()));
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		if (accepted)
		{
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
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateStealMonopoly(getActionOwner().getID(), targetSet.getID());
	}
}
