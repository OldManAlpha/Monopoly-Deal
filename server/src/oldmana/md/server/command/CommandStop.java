package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandStop extends Command
{
	public CommandStop()
	{
		super("stop", new String[] {"shutdown", "exit"}, new String[] {"/stop"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("Shutting server down", true);
		getServer().shutdown();
	}
}
