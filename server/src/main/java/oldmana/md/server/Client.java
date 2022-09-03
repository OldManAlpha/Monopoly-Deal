package oldmana.md.server;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.server.net.ConnectionThread;

public class Client
{
	private ConnectionThread net;
	private boolean connected;
	
	public Client(ConnectionThread net)
	{
		this.net = net;
		connected = net != null;
	}
	
	public void setNet(ConnectionThread net)
	{
		this.net = net;
		connected = net != null;
	}
	
	public ConnectionThread getNet()
	{
		return net;
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
	
	public void sendPacket(Packet packet)
	{
		if (net != null)
		{
			net.addOutPacket(packet);
		}
	}
}
