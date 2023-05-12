package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;

public class CommandBroadcast extends Command
{
	private final String PREFIX_COLOR = ChatColor.of(255, 160, 160);
	private final String MESSAGE_COLOR = ChatColor.of(240, 255, 255);
	
	public CommandBroadcast()
	{
		super("broadcast", null, new String[] {"/broadcast"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().broadcastMessage(PREFIX_COLOR + "[Server] " + MESSAGE_COLOR + getFullStringArgument(args, 0), true);
	}
}
