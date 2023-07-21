package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandToggleBot extends Command
{
	public CommandToggleBot()
	{
		super("togglebot", true);
		setUsage("/togglebot [Player ID]",
				"Player ID: The ID of the player to toggle bot status");
		setDescription("Toggles whether the player should make bot moves.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("You must specify the ID of the player.");
			return;
		}
		if (!verifyInt(args[0]))
		{
			sender.sendMessage("ID must be a number.");
			return;
		}
		Player player = getServer().getPlayerByID(parseInt(args[0]));
		if (player == null)
		{
			sender.sendMessage("No player by that ID.");
			return;
		}
		player.setBot(!player.isBot());
		sender.sendMessage(player.getName() + " is " + (player.isBot() ? "now" : "no longer") + " a bot.");
	}
}
