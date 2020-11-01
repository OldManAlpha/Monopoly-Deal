package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketStatus;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.md.server.Player;

public class ActionStateFinishTurn extends ActionState
{
	public ActionStateFinishTurn(Player player)
	{
		super(player);
		getServer().broadcastPacket(new PacketStatus("Waiting for " + player.getName() + " to finish their turn"));
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}
	
	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(getActionOwner().getID(), BasicActionState.FINISH_TURN, getActionOwner().getHand().getCardCount() - 7);
	}
}
