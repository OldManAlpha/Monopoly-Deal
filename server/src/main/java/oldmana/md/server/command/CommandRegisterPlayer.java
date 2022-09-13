package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandRegisterPlayer extends Command
{
	public CommandRegisterPlayer()
	{
		super("registerplayer", null, new String[] {"/registerplayer [Player Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
	
	}
}
