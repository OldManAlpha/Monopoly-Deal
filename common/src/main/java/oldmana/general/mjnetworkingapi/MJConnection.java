package oldmana.general.mjnetworkingapi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import oldmana.general.mjnetworkingapi.packet.Packet;

public class MJConnection
{
	private Socket socket;
	
	public MJConnection()
	{
		socket = new Socket();
	}
	
	public MJConnection(Socket s)
	{
		socket = s;
	}
	
	/**Connects to to the given IP and port. Waits until timeout is up.
	 * 
	 * @param ip - IP Address
	 * @param port - Port
	 * @param timeout - Time in MS until giving up connection.
	 * @throws IOException 
	 */
	public void connect(String ip, int port, int timeout) throws IOException
	{
		socket.connect(new InetSocketAddress(ip, port), timeout);
	}
	
	public void sendPacket(Packet p) throws Exception
	{
		Packet.sendPacket(socket, p);
	}
	
	public Packet receivePackets(int timeout) throws Exception
	{
		return Packet.receivePackets(socket, timeout);
	}
	
	public BufferedInputStream getInput()
	{
		try
		{
			return (BufferedInputStream) socket.getInputStream();
		}
		catch (IOException e) {}
		return null;
	}
	
	public BufferedOutputStream getOutput()
	{
		try
		{
			return (BufferedOutputStream) socket.getOutputStream();
		}
		catch (IOException e) {}
		return null;
	}
	
	public boolean hasAvailableInput()
	{
		try
		{
			return socket.getInputStream().available() > 0;
		}
		catch (Exception e) {}
		return false;
	}
	
	public boolean isClosed()
	{
		return socket.isClosed();
	}
	
	public void close() throws IOException
	{
		socket.close();
	}
	
	public Socket getSocket()
	{
		return socket;
	}
}
