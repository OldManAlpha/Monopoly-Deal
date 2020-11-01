package oldmana.general.md.server.state;

import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.general.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ActionStateDoNothing extends ActionState
{
	public ActionStateDoNothing()
	{
		super(null);
	}
	
	@Override
	public boolean isFinished()
	{
		return false;
	}

	@Override
	public Packet constructPacket()
	{
		return new PacketActionStateBasic(-1, BasicActionState.DO_NOTHING, 0);
	}

}
