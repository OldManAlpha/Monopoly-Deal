package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandSetMoves extends Command
{
	public CommandSetMoves()
	{
		super("setmoves", true);
		setAliases("setturns");
		setUsage("/setmoves [Moves]",
				"Moves: The number of moves the current player should have");
		setDescription("Sets the number of moves the current player has.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0 || !verifyInt(args[0]))
		{
			sendUsage(sender);
			return;
		}
		getServer().getGameState().setMoves(Integer.parseInt(args[0]));
		sender.sendMessage("Set number of moves to " + args[0], true);
	}
}
