package oldmana.md.server.command;

import oldmana.md.server.Bot;
import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;

public class CommandAddBot extends Command
{
	private int nextBotID = 1;
	
	public CommandAddBot()
	{
		super("addbot", null, new String[] {"/addbot <Name>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Bot bot = new Bot(getServer(), args.length > 0 ? getFullStringArgument(args, 0) : "Bot " + nextBotID++);
		getServer().addPlayer(bot);
		sender.sendMessage("Created bot '" + bot.getName() + "'");
		sender.sendMessage(ChatColor.PREFIX_ALERT + "Warning: Bots are currently experimental and may lead to " +
				"instability.");
	}
}
