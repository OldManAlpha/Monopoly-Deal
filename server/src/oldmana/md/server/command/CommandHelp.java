package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;

public class CommandHelp extends Command
{
	public CommandHelp()
	{
		super("help", null, new String[] {"/help <Command Name>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			Command cmd = getServer().getCommandHandler().findCommand(args[0]);
			if (cmd != null)
			{
				sender.sendMessage("Usage of " + cmd.getName() + ":");
				cmd.sendUsage(sender);
			}
			else
			{
				sender.sendMessage(ChatColor.PREFIX_ALERT + "Command not found.");
			}
		}
		else
		{
			sender.sendMessage("List of all commands:");
			for (Command cmd : getServer().getCommandHandler().getCommands())
			{
				cmd.sendUsage(sender);
			}
		}
	}
}
