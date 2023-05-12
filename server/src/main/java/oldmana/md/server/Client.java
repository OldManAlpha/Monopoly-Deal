package oldmana.md.server;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.server.net.ConnectionThread;

public class Client
{
	private ConnectionThread net;
	
	public Client(ConnectionThread net)
	{
		this.net = net;
	}
	
	public void setNet(ConnectionThread net)
	{
		this.net = net;
	}
	
	public ConnectionThread getNet()
	{
		return net;
	}
	
	public String getHostAddress()
	{
		return net.getConnection().getSocket().getInetAddress().getHostAddress();
	}
	
	public boolean isConnected()
	{
		return net != null;
	}
	
	public void sendPacket(Packet packet)
	{
		if (net != null)
		{
			net.addOutPacket(packet);
		}
	}
}
