package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry;

public class CommandDeop extends Command
{
	public CommandDeop()
	{
		super("deop", null, new String[] {"/deop [Player Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			PlayerRegistry registry = getServer().getPlayerRegistry();
			String name = getFullStringArgument(args, 0);
			Player player = getServer().getPlayerByName(name);
			if (player == null)
			{
				sender.sendMessage("Couldn't find an online player by that name!");
				return;
			}
			player.setOp(false);
			registry.getRegisteredPlayerByUUID(player.getUUID()).op = false;
			registry.savePlayers();
			sender.sendMessage("Removed operator permissions from " + player.getName() +".", true);
		}
		else
		{
			sender.sendMessage("Player name required.");
		}
	}
}
