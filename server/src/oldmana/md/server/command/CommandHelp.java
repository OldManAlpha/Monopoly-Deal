package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandHelp extends Command
{
	public CommandHelp()
	{
		super("help", null, new String[] {"/help"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("List of all commands:");
		for (Command cmd : getServer().getCommandHandler().getCommands())
		{
			cmd.sendUsage(sender);
		}
	}
}
