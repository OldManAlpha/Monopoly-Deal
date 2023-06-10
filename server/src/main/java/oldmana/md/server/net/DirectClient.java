package oldmana.md.server.net;

import oldmana.md.common.net.api.packet.Packet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This is used by Singleplayer to have direct access to the packets without using networking.
 */
public class DirectClient implements Client
{
	private final List<Packet> inPackets;
	private final List<Packet> outPackets;
	
	private volatile boolean close = false;
	
	private Consumer<Throwable> closeHandler;
	
	public DirectClient()
	{
		inPackets = new LinkedList<Packet>();
		outPackets = new LinkedList<Packet>();
	}
	
	public List<Packet> getInPacketsInstance()
	{
		return inPackets;
	}
	
	public List<Packet> getOutPacketsInstance()
	{
		return outPackets;
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
		return "Direct Connection";
	}
	
	@Override
	public boolean isConnected()
	{
		return !close;
	}
	
	@Override
	public void closeConnection()
	{
		close = true;
		if (closeHandler != null)
		{
			closeHandler.accept(new RuntimeException("Singleplayer exit"));
		}
	}
	
	@Override
	public void setCloseHandler(Consumer<Throwable> handler)
	{
		this.closeHandler = handler;
	}
}
