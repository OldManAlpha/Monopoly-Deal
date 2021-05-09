package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;

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
				int uid = parseInt(args[0]);
				RegisteredPlayer player = getServer().getPlayerRegistry().getRegisteredPlayerByUID(uid);
				if (player != null)
				{
					player.op = true;
					getServer().getPlayerRegistry().savePlayers();
					if (getServer().isPlayerWithUIDLoggedIn(uid))
					{
						getServer().getPlayerByUID(uid).setOp(true);
					}
					sender.sendMessage("Granted operator permissions to " + player.name +".", true);
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
