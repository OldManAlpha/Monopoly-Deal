package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.Player.StatusEffect;

public class CommandListEffects extends Command
{
	public CommandListEffects()
	{
		super("listeffects", true);
		setUsage("/listeffects [Player ID]",
				"Player ID: The ID of the player to see active effects on");
		setDescription("(Advanced) Displays all active status effects on the provided player.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (verifyInt(args[0]))
			{
				Player player = getServer().getPlayerByID(parseInt(args[0]));
				if (player != null)
				{
					sender.sendMessage("Status effects on " + player.getName() + ":");
					for (StatusEffect effect : player.getStatusEffects())
					{
						sender.sendMessage("- " + effect.getClass().getSimpleName());
					}
				}
				else
				{
					sender.sendMessage("Invalid player ID.");
				}
			}
			else
			{
				sender.sendMessage("Player ID must be a number.");
			}
		}
		else
		{
			sender.sendMessage("Player ID required.");
		}
	}
}
