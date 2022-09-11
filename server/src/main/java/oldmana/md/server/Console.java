package oldmana.md.server;

public class Console implements CommandSender
{
	@Override
	public void sendMessage(String message)
	{
		System.out.println(ChatColor.stripFormatting(message));
	}
	
	@Override
	public boolean isOp()
	{
		return true;
	}
}
