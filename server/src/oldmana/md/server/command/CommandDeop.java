package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;
import oldmana.md.server.status.StatusEffect;

public class CommandDeop extends Command
{
	public CommandDeop()
	{
		super("deop", null, new String[] {"/deop [Player UID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (verifyInt(args[0]))
			{
				RegisteredPlayer player = getServer().getPlayerRegistry().getRegisteredPlayerByUID(parseInt(args[0]));
				if (player != null)
				{
					player.op = false;
					getServer().getPlayerRegistry().savePlayers();
					sender.sendMessage("Removed operator permissions from " + player.name +".");
				}
				else
				{
					sender.sendMessage("Invalid player UID.");
				}
			}
			else
			{
				sender.sendMessage("Player UID must be a number.");
			}
		}
		else
		{
			sender.sendMessage("Player UID required.");
		}
	}
}
