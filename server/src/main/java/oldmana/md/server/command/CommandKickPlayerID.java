package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandKickPlayerID extends Command
{
	public CommandKickPlayerID()
	{
		super("kickid", null, new String[] {"/kickid [Player ID] <Reason>"}, true);
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
