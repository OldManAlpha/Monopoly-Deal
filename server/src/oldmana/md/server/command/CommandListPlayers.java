package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandListPlayers extends Command
{
	public CommandListPlayers()
	{
		super("listplayers", null, new String[] {"/listplayers"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("List of players:");
		sender.sendMessage("");
		for (Player player : getServer().getPlayers())
		{
			sender.sendMessage("- " + player.getName() + "(ID: " + player.getID() + ")" + (player.isOnline() ? "" : " (Offline)"));
			sender.sendMessage("Hand ID: " + player.getHand().getID());
			sender.sendMessage("Bank ID: " + player.getBank().getID());
		}
	}
}
