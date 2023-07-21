package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.rules.ValueType;

public class CommandSetTurn extends Command
{
	public CommandSetTurn()
	{
		super("setturn", true);
		setUsage("/setturn [Player ID] <Draw?>",
				"Player ID: The ID of the player to give the turn to",
				"Draw (Optional): Whether the player should be able to draw");
		setDescription("Sets whose turn it is right now, optionally specifying whether they should draw.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			boolean draw = true;
			if (args.length >= 2)
			{
				draw = ValueType.BOOLEAN.parse(args[1]);
			}
			Player player = getServer().getPlayerByID(Integer.parseInt(args[0]));
			if (player == null)
			{
				sender.sendMessage(ChatColor.PREFIX_ALERT + "There is no player by that ID.");
				return;
			}
			getServer().getGameState().setTurn(player, draw);
			sender.sendMessage("Set it to be " + player.getName() + "'s turn");
		}
	}
}
