package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandNextTurn extends Command
{
	public CommandNextTurn()
	{
		super("nextturn", null, new String[] {"/nextturn"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getGameState().nextTurn();
		sender.sendMessage("Now " + getServer().getGameState().getActivePlayer().getName() + "'s turn", true);
	}
}
