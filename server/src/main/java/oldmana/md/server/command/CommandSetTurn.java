package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;

public class CommandSetTurn extends Command
{
	public CommandSetTurn()
	{
		super("setturn", null, new String[] {"/setturn [Player ID] [Draw(true/false)]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			boolean draw = true;
			if (args.length >= 2)
			{
				if (!verifyBoolean(args[1]))
				{
					sendUsage(sender);
					return;
				}
				draw = Boolean.parseBoolean(args[1]);
			}
			Player player = getServer().getPlayerByID(Integer.parseInt(args[0]));
			getServer().getGameState().setTurn(getServer().getPlayerByID(Integer.parseInt(args[0])), draw);
			sender.sendMessage("Set it to be " + player.getName() + "'s turn");
		}
	}
}
