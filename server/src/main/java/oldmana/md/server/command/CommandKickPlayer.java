package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandKickPlayer extends Command
{
	public CommandKickPlayer()
	{
		super("kick", true);
		setAliases("kickplayer");
		setUsage("/kick [Player Name]",
				"Player Name: The name of the player to kick from the game");
		setDescription("Removes a player from the game.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		String name = getFullStringArgument(args, 0);
		Player player = getServer().getPlayerByName(name);
		if (player == null)
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Could not find player by name '" + name + "'");
			return;
		}
		getServer().kickPlayer(player, "Kicked by operator");
		sender.sendMessage("Kicked player " + player.getDescription());
	}
}
