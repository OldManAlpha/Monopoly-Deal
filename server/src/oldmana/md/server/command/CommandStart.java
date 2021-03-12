package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Console;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.state.GameState;

public class CommandStart extends Command
{
	public CommandStart()
	{
		super("start", null, new String[] {"/start"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		System.out.println("Starting game");
		if (!(sender instanceof Console))
		{
			sender.sendMessage("Starting game");
		}
		MDServer server = getServer();
		for (int i = 0 ; i < 5 ; i++)
		{
			for (Player player : server.getPlayers())
			{
				server.getDeck().drawCard(player, 1.8);
			}
		}
		GameState gs = server.getGameState();
		gs.nextTurn();
	}
}
