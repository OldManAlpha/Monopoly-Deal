package oldmana.md.server.command;

import oldmana.md.common.playerui.ChatAlignment;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelp extends Command
{
	public static final String CATEGORY = "help";
	
	private static final int PAGE_LIMIT = 10;
	
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
			sender.clearMessages(CATEGORY);
			
			List<Command> cmds = getServer().getCommandHandler().getCommands().stream()
					.filter(cmd -> cmd.checkPermission(sender))
					.collect(Collectors.toList());
			int page = args.length > 0 ? parseInt(args[0]) : 0;
			int maxPage = (int) Math.max(Math.ceil(cmds.size() / (double) PAGE_LIMIT) - 1, 0);
			page = Math.max(page, 0);
			page = Math.min(page, maxPage);
			
			sender.sendMessage("", CATEGORY);
			sender.sendMessage(new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY).startUnderline()
					.add(ChatColor.LIGHT_GREEN + "Commands Help").build());
			int bound = Math.min((page + 1) * PAGE_LIMIT, cmds.size());
			for (int i = page * PAGE_LIMIT ; i < bound ; i++)
			{
				cmds.get(i).sendInfo(sender);
			}
			MessageBuilder mb = new MessageBuilder(ChatAlignment.CENTER).setCategory(CATEGORY);
			mb.addCommand(ChatColor.LINK + "[Prev]", "help " + (page - 1));
			mb.add(ChatColor.LIGHT_ORANGE + "    Page " + (page + 1) + " of " + (maxPage + 1) + "    ");
			mb.addCommand(ChatColor.LINK + "[Next]", "help " + (page + 1));
			sender.sendMessage(mb.build());
			return;
		}
		
		Command cmd = getServer().getCommandHandler().findCommand(args[0]);
		if (cmd == null)
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Could not find command '" + args[0] + "'.");
			return;
		}
		if (!cmd.checkPermission(sender))
		{
			sendInsufficientPermissions(sender);
			return;
		}
		
		cmd.sendUsage(sender);
	}
}
