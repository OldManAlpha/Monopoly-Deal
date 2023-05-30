package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.PlayerRegistry;

public class CommandOp extends Command
{
	public CommandOp()
	{
		super("op", null, new String[] {"/op [Player Name]"}, true);
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
			player.setOp(true);
			registry.getRegisteredPlayerByUUID(player.getUUID()).op = true;
			registry.savePlayers();
			sender.sendMessage("Granted operator permissions to " + player.getName() +".", true);
			if (!getServer().isIntegrated())
			{
				player.sendMessage(ChatColor.PREFIX_ALERT + "You are now an operator");
			}
		}
		else
		{
			sender.sendMessage("Player name required.");
		}
	}
}
