package oldmana.general.md.server.net;

import java.io.IOException;

import oldmana.general.md.server.Client;
import oldmana.general.md.server.MDServer;
import oldmana.general.mjnetworkingapi.server.MJServer;
import oldmana.general.mjnetworkingapi.server.MJServer.MJConnectAttempt;

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
