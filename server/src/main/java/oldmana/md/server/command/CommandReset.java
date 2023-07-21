package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandReset extends Command
{
	public CommandReset()
	{
		super("reset", true);
		setAliases("resetgame");
		setDescription("Puts all cards back in the deck and resets the game.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (getServer().getGameState().isGameRunning())
		{
			getServer().getGameState().endGame();
		}
		getServer().getGameState().cleanup();
		sender.sendMessage("The game has been reset", true);
	}
}
