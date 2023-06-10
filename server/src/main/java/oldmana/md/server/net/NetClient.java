package oldmana.md.server.net;

import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.packet.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class NetClient extends Thread implements Client
{
	private MJConnection connection;
	
	private final List<Packet> inPackets;
	private final List<Packet> outPackets;
	
	private volatile boolean close = false;
	
	private Consumer<Throwable> closeHandler;
	
	public NetClient(MJConnection connection)
	{
		this.connection = connection;
		inPackets = new LinkedList<Packet>();
		outPackets = new LinkedList<Packet>();
		start();
	}
	
	@Override
	public void closeConnection()
	{
		close = true;
	}
	
	@Override
	public void setCloseHandler(Consumer<Throwable> handler)
	{
		closeHandler = handler;
	}
	
	@Override
	public void addInPacket(Packet p)
	{
		synchronized (inPackets)
		{
			inPackets.add(p);
		}
	}
	
	@Override
	public List<Packet> getInPackets()
	{
		synchronized (inPackets)
		{
			List<Packet> copy = new ArrayList<Packet>(inPackets);
			inPackets.clear();
			return copy;
		}
	}
	
	@Override
	public void addOutPacket(Packet p)
	{
		synchronized (outPackets)
		{
			outPackets.add(p);
		}
	}
	
	@Override
	public List<Packet> getOutPackets()
	{
		synchronized (outPackets)
		{
			List<Packet> copy = new ArrayList<Packet>(outPackets);
			outPackets.clear();
			return copy;
		}
	}
	
	@Override
	public boolean hasOutPackets()
	{
		synchronized (outPackets)
		{
			return !outPackets.isEmpty();
		}
	}
	
	@Override
	public String getHostAddress()
	{
		return connection != null ? connection.getSocket().getInetAddress().getHostAddress() : "Unknown Address";
	}
	
	@Override
	public boolean isConnected()
	{
		return connection != null && !connection.isClosed();
	}
	
	@Override
	public void run()
	{
		try
		{
			while (!connection.isClosed())
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
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e) {}
			}
			throw new GracefulDisconnect();
		}
		catch (Exception | Error e)
		{
			if (!(e instanceof GracefulDisconnect))
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
			if (closeHandler != null)
			{
				closeHandler.accept(e);
			}
		}
	}
	
	public static class GracefulDisconnect extends RuntimeException {}
}
