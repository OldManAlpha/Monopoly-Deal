package oldmana.md.server;

public interface CommandSender
{
	void sendMessage(String message);
	
	default void sendMessage(String message, boolean printConsole)
	{
		sendMessage(message);
		if (printConsole && !(this instanceof Console))
		{
			System.out.println(ChatColor.stripFormatting(message));
		}
	}
	
	boolean isOp();
}
