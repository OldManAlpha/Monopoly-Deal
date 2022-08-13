package oldmana.md.server.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.packet.Packet;

public class ConnectionThread extends Thread
{
	private MJConnection connection;
	
	private List<Packet> inPackets;
	private List<Packet> outPackets;
	
	private boolean close = false;
	
	public ConnectionThread(MJConnection connection)
	{
		this.connection = connection;
		
		inPackets = Collections.synchronizedList(new ArrayList<Packet>());
		outPackets = Collections.synchronizedList(new ArrayList<Packet>());
		
		start();
	}
	
	public void addInPacket(Packet p)
	{
		synchronized (inPackets)
		{
			inPackets.add(p);
		}
	}
	
	public List<Packet> getInPackets()
	{
		synchronized (inPackets)
		{
			List<Packet> copy = new ArrayList<Packet>(inPackets);
			inPackets.clear();
			return copy;
		}
	}
	
	public void addOutPacket(Packet p)
	{
		synchronized (outPackets)
		{
			outPackets.add(p);
		}
	}
	
	public List<Packet> getOutPackets()
	{
		synchronized (outPackets)
		{
			List<Packet> copy = new ArrayList<Packet>(outPackets);
			outPackets.clear();
			return copy;
		}
	}
	
	public boolean hasOutPackets()
	{
		synchronized (outPackets)
		{
			return !outPackets.isEmpty();
		}
	}
	
	public void close()
	{
		close = true;
	}
	
	public boolean isClosed()
	{
		return connection.isClosed();
	}
	
	@Override
	public void run()
	{
		while (!connection.isClosed())
		{
			try
			{
				for (Packet p : getOutPackets())
				{
					connection.sendPacket(p);
				}
				
				while (connection.hasAvailableInput())
				{
					Packet p = connection.receivePackets(10000);
					addInPacket(p);
				}
				
				if (close)
				{
					connection.close();
				}
			}
			catch (Exception e)
			{
				System.err.println(connection.getSocket().getInetAddress().getHostAddress() + " lost connection");
				e.printStackTrace();
				try
				{
					connection.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e) {}
		}
	}
}
