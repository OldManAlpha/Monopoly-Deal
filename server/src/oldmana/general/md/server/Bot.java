package oldmana.general.md.server;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class Bot extends Player
{

	public Bot(MDServer server, String name)
	{
		super(server, 0, null, name);
	}
	
	@Override
	public void sendPacket(Packet packet) {}
}
