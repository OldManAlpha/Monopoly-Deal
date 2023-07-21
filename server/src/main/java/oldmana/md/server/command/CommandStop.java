package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandStop extends Command
{
	public CommandStop()
	{
		super("stop", true);
		setAliases("shutdown", "exit");
		setDescription("Disconnects all players and shuts down the server.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("Shutting server down", true);
		getServer().shutdown();
	}
}
