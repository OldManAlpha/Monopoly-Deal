package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;

public class CommandListRegisteredPlayers extends Command
{
	public CommandListRegisteredPlayers()
	{
		super("listregisteredplayers", true);
		setAliases("registeredplayers", "listregistered");
		setDescription("Lists all players that have logged into this server.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		sender.sendMessage("List of registered players:");
		for (RegisteredPlayer player : getServer().getPlayerRegistry().getRegisteredPlayers())
		{
			sender.sendMessage("- " + player.name + (player.op ? " (Operator)" : ""));
		}
	}
}
