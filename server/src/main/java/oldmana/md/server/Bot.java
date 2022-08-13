package oldmana.md.server;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.server.ai.BasicAI;
import oldmana.md.server.ai.PlayerAI;

public class Bot extends Player
{
	private PlayerAI ai;
	
	public Bot(MDServer server, String name)
	{
		super(server, -1, null, name, false);
		setAI(new BasicAI(this));
	}
	
	public void setAI(PlayerAI ai)
	{
		this.ai = ai;
	}
	
	public PlayerAI getAI()
	{
		return ai;
	}
	
	public void doAIAction()
	{
		ai.doAction();
	}
	
	@Override
	public void sendPacket(Packet packet) {}
}
