package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandNextTurn extends Command
{
	public CommandNextTurn()
	{
		super("nextturn", true);
		setDescription("Immediately ends the current turn and moves on to the next player.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getGameState().nextTurn();
		sender.sendMessage("Now " + getServer().getGameState().getActivePlayer().getName() + "'s turn", true);
	}
}
