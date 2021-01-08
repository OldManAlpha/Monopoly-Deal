package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.PlayerRegistry;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;

public class CommandUnregisterPlayer extends Command
{
	public CommandUnregisterPlayer()
	{
		super("unregisterplayer", null, new String[] {"/unregisterplayer [Player UID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			PlayerRegistry registry = getServer().getPlayerRegistry();
			if (verifyInt(args[0]))
			{
				RegisteredPlayer player = registry.getRegisteredPlayerByUID(Integer.parseInt(args[0]));
				if (player != null)
				{
					registry.unregisterPlayer(player);
					registry.savePlayers();
					sender.sendMessage("Unregistered player '" + player.name + "'");
				}
				else
				{
					sender.sendMessage("There is no player by that UID.");
				}
			}
			else
			{
				sender.sendMessage("UID must be an integer.");
			}
		}
		else
		{
			sender.sendMessage("UID required.");
		}
	}
}
