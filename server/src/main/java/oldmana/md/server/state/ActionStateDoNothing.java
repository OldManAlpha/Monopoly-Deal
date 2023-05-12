package oldmana.md.server.state;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic;
import oldmana.md.net.packet.server.actionstate.PacketActionStateBasic.BasicActionState;

public class ActionStateDoNothing extends ActionState
{
	public ActionStateDoNothing()
	{
		super(null);
	}
	
	public ActionStateDoNothing(String status)
	{
		this();
		setStatus(status);
	}
	
	@Override
	public boolean isImportant()
	{
		return false;
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
