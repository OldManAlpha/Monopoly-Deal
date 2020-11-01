package oldmana.md.server.net;

import java.io.IOException;

import oldmana.general.mjnetworkingapi.server.MJServer;
import oldmana.general.mjnetworkingapi.server.MJServer.MJConnectAttempt;
import oldmana.md.server.Client;
import oldmana.md.server.MDServer;

public class IncomingConnectionsThread extends Thread
{
	private MJServer server;
	
	public IncomingConnectionsThread()
	{
		try
		{
			server = new MJServer(7777);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		start();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			MJConnectAttempt attempt = server.listen();
			System.out.println("Client attempting connection from " + attempt.getConnection().getSocket().getInetAddress().toString());
			if (attempt.successful())
			{
				MDServer.getInstance().addClient(new Client(new ConnectionThread(attempt.getConnection())));
			}
		}
	}
}
