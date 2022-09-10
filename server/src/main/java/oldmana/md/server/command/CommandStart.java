package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
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
		sender.sendMessage("Starting game", true);
		MDServer server = getServer();
		for (int i = 0 ; i < 5 ; i++)
		{
			for (Player player : server.getPlayers())
			{
				server.getDeck().drawCard(player, 0.6);
			}
		}
		GameState gs = server.getGameState();
		gs.nextTurn();
	}
}
