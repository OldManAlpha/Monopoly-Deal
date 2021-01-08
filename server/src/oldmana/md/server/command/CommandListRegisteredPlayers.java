package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;

public class CommandListRegisteredPlayers extends Command
{
	public CommandListRegisteredPlayers()
	{
		super("listregisteredplayers", new String[] {"registeredplayers", "listregistered"}, new String[] {"/listregisteredplayers"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("List of registered players:");
		for (RegisteredPlayer player : getServer().getPlayerRegistry().getRegisteredPlayers())
		{
			sender.sendMessage("- " + player.name + (player.op ? " (Operator)" : "") + ": " + player.uid);
		}
	}
}
