package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelp extends Command
{
	private static final int PAGE_LIMIT = 6;
	
	public CommandHelp()
	{
		super("help", false);
		setAliases("?");
		setUsage("/help <Command Name>",
				"Command Name (Optional): The name of the command to get help on.");
		setDescription("View a list of available commands, or specify a specific command to get more details on it.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0 || verifyInt(args[0]))
		{
			if (sender instanceof Player)
			{
				((Player) sender).clearMessages("help");
			}
			List<Command> cmds = getServer().getCommandHandler().getCommands().stream()
					.filter(cmd -> cmd.checkPermission(sender))
					.collect(Collectors.toList());
			int page = args.length > 0 ? parseInt(args[0]) : 0;
			int maxPage = (int) Math.max(Math.ceil(cmds.size() / (double) PAGE_LIMIT) - 1, 0);
			page = Math.max(page, 0);
			page = Math.min(page, maxPage);
			
			sender.sendMessage(ChatColor.LIGHT_GREEN + "---- Commands ----", "help");
			int bound = Math.min((page + 1) * PAGE_LIMIT, cmds.size());
			for (int i = page * PAGE_LIMIT ; i < bound ; i++)
			{
				cmds.get(i).sendInfo(sender);
			}
			MessageBuilder mb = new MessageBuilder("    ").setCategory("help");
			mb.addCommand(ChatColor.LINK + "[Prev]", "help " + (page - 1));
			mb.add(ChatColor.LIGHT_ORANGE + "    Page " + (page + 1) + " of " + (maxPage + 1) + "    ");
			mb.addCommand(ChatColor.LINK + "[Next]", "help " + (page + 1));
			sender.sendMessage(mb.build());
			
			/*
			sender.sendMessage("List of all available commands:");
			for (Command cmd : getServer().getCommandHandler().getCommands())
			{
				if (cmd.checkPermission(sender))
				{
					cmd.sendInfo(sender);
				}
			}
			 */
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
		
		cmd.sendUsage(sender);
	}
}
