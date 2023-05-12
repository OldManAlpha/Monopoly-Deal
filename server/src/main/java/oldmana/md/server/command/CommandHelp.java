package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;

public class CommandHelp extends Command
{
	public CommandHelp()
	{
		super("help", new String[] {"?"}, new String[] {"/help <Command Name>"}, false);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("List of all available commands:");
			for (Command cmd : getServer().getCommandHandler().getCommands())
			{
				if (cmd.checkPermission(sender))
				{
					cmd.sendUsage(sender);
				}
			}
			return;
		}
		
		Command cmd = getServer().getCommandHandler().findCommand(args[0]);
		if (cmd == null)
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Command not found.");
			return;
		}
		if (!cmd.checkPermission(sender))
		{
			sender.sendMessage("Insufficient permissions.");
			return;
		}
		
		sender.sendMessage("Usage of " + cmd.getName() + ":");
		cmd.sendUsage(sender);
	}
}
