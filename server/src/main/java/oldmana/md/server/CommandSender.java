package oldmana.md.server;

import oldmana.md.common.Message;

public interface CommandSender
{
	void sendMessage(String message);
	
	void sendMessage(String message, String category);
	
	default void sendMessage(String message, boolean printConsole)
	{
		sendMessage(message);
		if (printConsole)
		{
			MDServer.getInstance().getConsoleSender().sendMessage(message);
		}
	}
	
	default void sendMessage(Message message)
	{
		sendMessage(message.getUnformattedMessage());
	}
	
	default void sendMessage(Message message, boolean printConsole)
	{
		sendMessage(message.getUnformattedMessage(), printConsole);
	}
	
	boolean isOp();
}
