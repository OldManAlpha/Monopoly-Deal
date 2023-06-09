package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;

public class CommandStart extends Command
{
	public CommandStart()
	{
		super("start", new String[] {"startgame", "deal"}, new String[] {"/start"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length > 0 && args[0].equalsIgnoreCase("force"))
		{
			sender.sendMessage("Starting game (Forced)", true);
			getServer().getGameState().startGame(true);
			return;
		}
		if (getServer().getGameState().isGameRunning())
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "The game is already running! Use /reset to end the game first.");
			return;
		}
		if (getServer().getPlayerCount() == 0)
		{
			sender.sendMessage("Cannot start the game with zero players!");
			return;
		}
		sender.sendMessage("Starting game", true);
		getServer().getGameState().startGame();
	}
}
