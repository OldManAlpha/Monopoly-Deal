package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandSetTurns extends Command
{
	public CommandSetTurns()
	{
		super("setturns", null, new String[] {"/setturns [Number of Turns]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getGameState().setTurns(Integer.parseInt(args[0]));
		getServer().getGameState().nextNaturalActionState();
		sender.sendMessage("Set number of turns to " + args[0], true);
	}
}
