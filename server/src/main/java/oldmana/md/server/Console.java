package oldmana.md.server;

import oldmana.md.common.Message;

public class Console implements CommandSender
{
	@Override
	public void sendMessage(String message)
	{
		System.out.println(ChatColor.stripFormatting(message));
	}
	
	@Override
	public void sendMessage(String message, boolean printConsole)
	{
		sendMessage(message);
	}
	
	@Override
	public boolean isOp()
	{
		return true;
	}
}
