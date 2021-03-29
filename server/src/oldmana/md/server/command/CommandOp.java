package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;
import oldmana.md.server.status.StatusEffect;

public class CommandOp extends Command
{
	public CommandOp()
	{
		super("op", null, new String[] {"/op [Player UID]"}, true);
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
					player.op = true;
					getServer().getPlayerRegistry().savePlayers();
					sender.sendMessage("Granted operator permissions to " + player.name +".");
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
