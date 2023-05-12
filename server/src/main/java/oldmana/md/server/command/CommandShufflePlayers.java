package oldmana.md.server.command;

import oldmana.md.server.CommandSender;

public class CommandShufflePlayers extends Command
{
	public CommandShufflePlayers()
	{
		super("shuffleplayers", null, new String[] {"/shuffleplayers"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		getServer().getGameState().getTurnOrder().shuffle();
		sender.sendMessage("Shuffled player turn order.", true);
	}
}
