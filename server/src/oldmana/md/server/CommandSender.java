package oldmana.md.server;

public interface CommandSender
{
	public void sendMessage(String message);
	
	public default void sendMessage(String message, boolean printConsole)
	{
		sendMessage(message);
		if (printConsole && !(this instanceof Console))
		{
			System.out.println(ChatColor.stripColors(message));
		}
	}
	
	public boolean isOp();
}
