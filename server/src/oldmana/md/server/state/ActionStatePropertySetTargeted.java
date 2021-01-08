package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateStealMonopoly;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.PropertySet;

public class ActionStatePropertySetTargeted extends ActionState
{
	private PropertySet targetSet;
	
	public ActionStatePropertySetTargeted(Player player, PropertySet targetSet)
	{
		super(player, targetSet.getOwner());
		this.targetSet = targetSet;
	}
	
	public PropertySet getTargetSet()
	{
		return targetSet;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateStealMonopoly(getActionOwner().getID(), targetSet.getID());
	}
}
