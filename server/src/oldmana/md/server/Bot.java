package oldmana.md.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class Bot extends Player
{
	public Bot()
	{
		super(null, 0, null, null, false);
	}
	
	
	
	@Override
	public void sendPacket(Packet packet) {}
}
