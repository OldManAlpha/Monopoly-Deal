package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandBroadcast extends Command
{
	public CommandBroadcast()
	{
		super("broadcast", null, new String[] {"/broadcast"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().broadcastMessage("[Server] " + getFullStringArgument(args, 0), true);
	}
}
