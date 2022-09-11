package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandReset extends Command
{
	public CommandReset()
	{
		super("reset", new String[] {"resetgame"}, new String[] {"/reset"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getGameState().endGame();
		getServer().getGameState().cleanup();
		sender.sendMessage("The game has been reset", true);
	}
}
