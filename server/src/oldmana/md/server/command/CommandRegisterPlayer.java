package oldmana.md.server.command;

import java.util.Random;

import oldmana.md.server.CommandSender;
import oldmana.md.server.PlayerRegistry;
import oldmana.md.server.PlayerRegistry.RegisteredPlayer;

public class CommandRegisterPlayer extends Command
{
	public CommandRegisterPlayer()
	{
		super("registerplayer", null, new String[] {"/registerplayer [Player Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			String name = getFullStringArgument(args, 0).replace(',', ' ');
			RegisteredPlayer player = new RegisteredPlayer(new Random().nextInt(10000000), name, false);
			PlayerRegistry registry = getServer().getPlayerRegistry();
			registry.registerPlayer(player);
			registry.savePlayers();
			sender.sendMessage("Registered player '" + name + "' with the User ID " + player.uid, true);
			sender.sendMessage("Give this ID to the player intended to use it.", true);
		}
		else
		{
			sender.sendMessage("Name required.");
		}
	}
}
