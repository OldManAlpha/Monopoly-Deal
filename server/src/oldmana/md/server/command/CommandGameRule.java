package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.GameRules;

public class CommandGameRule extends Command
{
	public CommandGameRule()
	{
		super("gamerule", null, new String[] {"/gamerule setwin [Monopoly Count]", "/gamerule rentall [true/false]", "/gamerule dealbreakerdiscard [true/false]"}, 
				true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		GameRules rules = getServer().getGameRules();
		
		if (args.length > 1)
		{
			if (args[0].equalsIgnoreCase("setwin"))
			{
				rules.setMonopoliesRequiredToWin(Integer.parseInt(args[1]));
				sender.sendMessage("Set required monopolies to win to " + rules.getMonopoliesRequiredToWin());
			}
			else if (args[0].equalsIgnoreCase("rentall"))
			{
				boolean rentAll = Boolean.parseBoolean(args[1]);
				rules.setDoesRentChargeAll(rentAll);
				sender.sendMessage("Rent charges all: " + rentAll);
			}
			else if (args[0].equalsIgnoreCase("dealbreakerdiscard"))
			{
				boolean dealBreakerDiscard = Boolean.parseBoolean(args[1]);;
				rules.setDoDealBreakersDiscardSets(dealBreakerDiscard);
				sender.sendMessage("Deal breakers discard: " + dealBreakerDiscard);
			}
			else
			{
				sender.sendMessage("Unknown gamerule '" + args[0] + "'");
			}
		}
		else
		{
			sender.sendMessage("Insufficient arguments");
		}
	}
}
