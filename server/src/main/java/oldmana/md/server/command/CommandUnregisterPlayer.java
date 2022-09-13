package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandUnregisterPlayer extends Command
{
	public CommandUnregisterPlayer()
	{
		super("unregisterplayer", null, new String[] {"/unregisterplayer [Player UID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
	
	}
}
