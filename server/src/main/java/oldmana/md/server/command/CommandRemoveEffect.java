package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.Player.StatusEffect;

public class CommandRemoveEffect extends Command
{
	public CommandRemoveEffect()
	{
		super("removeeffect", null, new String[] {"/removeeffect [Player ID] [Effect Index]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 2)
		{
			if (verifyInt(args[0]) && verifyInt(args[1]))
			{
				Player player = getServer().getPlayerByID(parseInt(args[0]));
				if (player != null)
				{
					StatusEffect effect = player.getStatusEffect(parseInt(args[1]));
					player.removeStatusEffect(effect);
					sender.sendMessage("Removed effect " + effect.getClass().getSimpleName() + " from " + player.getName(), true);
				}
				else
				{
					sender.sendMessage("Invalid player ID.");
				}
			}
			else
			{
				sender.sendMessage("Player ID and effect index must be a number.");
			}
		}
		else
		{
			sender.sendMessage("Player ID and effect index required.");
		}
	}
}
