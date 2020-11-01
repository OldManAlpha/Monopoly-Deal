package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

public class ActionStateDraw extends ActionState
{
	public ActionStateDraw(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus(player.getName() + "'s Turn: Draw"));
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.DRAW, 0);
	}
}
