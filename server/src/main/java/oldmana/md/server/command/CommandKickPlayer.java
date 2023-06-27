package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandKickPlayer extends Command
{
	public CommandKickPlayer()
	{
		super("kick", new String[] {"kickplayer"}, new String[] {"/kick [Player Name]"}, true);
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
