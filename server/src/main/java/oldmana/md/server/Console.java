package oldmana.md.server;

import oldmana.md.common.playerui.ChatAlignment;

public class Console implements CommandSender
{
	@Override
	public void sendMessage(String message)
	{
		System.out.println(ChatColor.stripFormatting(message));
	}
	
	@Override
	public void sendMessage(String message, String category)
	{
		sendMessage(message);
	}
	
	@Override
	public void sendMessage(String message, ChatAlignment alignment)
	{
		sendMessage(message);
	}
	
	@Override
	public void sendMessage(String message, ChatAlignment alignment, String category)
	{
		sendMessage(message);
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
