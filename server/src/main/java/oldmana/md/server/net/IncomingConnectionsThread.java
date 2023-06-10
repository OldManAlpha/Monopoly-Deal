package oldmana.md.server.net;

import java.io.IOException;
import java.net.ServerSocket;

import oldmana.md.common.net.api.server.MJServer;
import oldmana.md.common.net.api.server.MJServer.MJConnectAttempt;
import oldmana.md.server.MDServer;

public class IncomingConnectionsThread extends Thread
{
	private MJServer server;
	
	public IncomingConnectionsThread(int port) throws IOException
	{
		server = new MJServer(port);
		setDaemon(true);
		start();
	}
	
	public ServerSocket getSocket()
	{
		return server.getServerSocket();
	}
	
	@Override
	public void run()
	{
		while (!MDServer.getInstance().isShuttingDown())
		{
			try
			{
				MJConnectAttempt attempt = server.listen();
				System.out.println("Client attempting connection from " + attempt.getConnection().getSocket().getInetAddress().getHostAddress());
				if (attempt.successful())
				{
					MDServer.getInstance().addClient(new NetClient(attempt.getConnection()));
				}
			}
			catch (Exception e) {}
		}
	}
}
