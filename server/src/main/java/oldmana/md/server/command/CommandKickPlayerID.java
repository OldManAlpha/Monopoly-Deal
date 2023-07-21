package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandKickPlayerID extends Command
{
	public CommandKickPlayerID()
	{
		super("kickid", true);
		setUsage("/kickid [Player ID] <Reason>",
				"Player ID: The numerical ID of the player to kick",
				"Reason (Optional): The reason to kick the player");
		setDescription("Removes a player with the provided ID from the game, optionally specifying a reason.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Player player = getServer().getPlayerByID(Integer.parseInt(args[0]));
		String reason = "Kicked by operator";
		if (args.length > 1)
		{
			reason = getFullStringArgument(args, 1);
		}
		getServer().kickPlayer(player, reason);
		sender.sendMessage("Kicked player " + player.getDescription() + " for '" + reason + "'", true);
	}
}
