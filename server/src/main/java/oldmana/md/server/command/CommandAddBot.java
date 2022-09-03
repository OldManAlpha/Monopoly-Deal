package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.card.collection.deck.VanillaDeck;
import oldmana.md.server.event.PlayerJoinedEvent;

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
		Player bot = new Player(getServer(), args.length > 0 ? getFullStringArgument(args, 0) : "Bot " + nextBotID++);
		getServer().addPlayer(bot);
		getServer().getEventManager().callEvent(new PlayerJoinedEvent(bot));
		sender.sendMessage("Created bot '" + bot.getName() + "' (ID: " + bot.getID() + ")");
		if (!(getServer().getDeck().getDeckStack() instanceof VanillaDeck))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Warning: Bots may not function well and cause high resource " +
					"usage with custom decks.");
		}
	}
}
