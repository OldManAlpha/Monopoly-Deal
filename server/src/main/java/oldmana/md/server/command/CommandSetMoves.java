package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandSetMoves extends Command
{
	public CommandSetMoves()
	{
		super("setmoves", new String[] {"setturns"}, new String[] {"/setmoves [Number of Moves]"}, true);
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
