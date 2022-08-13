package oldmana.md.client.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.client.MDClient;
import oldmana.md.client.gui.screen.TableScreen;

public class ConnectionThread extends Thread
{
	private MJConnection connection;
	
	private List<Packet> inPackets;
	private List<Packet> outPackets;
	
	private volatile boolean sendingPackets;
	
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
			return outPackets.size() > 0;
		}
	}
	
	public boolean isSendingPackets()
	{
		return sendingPackets;
	}
	
	public void close()
	{
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		while (!connection.isClosed())
		{
			try
			{
				sendingPackets = true;
				for (Packet p : getOutPackets())
				{
					connection.sendPacket(p);
				}
				sendingPackets = false;
				
				while (connection.hasAvailableInput())
				{
					Packet p = connection.receivePackets(10000);
					addInPacket(p);
				}
			}
			catch (Exception e)
			{
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
		SwingUtilities.invokeLater(() ->
		{
			TableScreen ts = MDClient.getInstance().getTableScreen();
			ts.getTopbar().setText("Lost Connection");
			ts.repaint();
		});
	}
}
