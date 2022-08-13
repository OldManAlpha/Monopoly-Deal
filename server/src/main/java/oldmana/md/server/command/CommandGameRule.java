package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.rules.GameRules;
import oldmana.md.server.rules.win.ColorCondition;
import oldmana.md.server.rules.win.MoneyCondition;
import oldmana.md.server.rules.win.PropertySetCondition;

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
				if (args.length > 2 && verifyInt(args[2]))
				{
					int count = parseInt(args[2]);
					if (args[1].equalsIgnoreCase("monopoly"))
					{
						rules.setWinCondition(new PropertySetCondition(count));
						sender.sendMessage("Set win condition to: Monopoly x" + count, true);
					}
					else if (args[1].equalsIgnoreCase("color") || args[1].equalsIgnoreCase("colors"))
					{
						rules.setWinCondition(new ColorCondition(count));
						sender.sendMessage("Set win condition to: Property Color x" + count, true);
					}
					else if (args[1].equalsIgnoreCase("money"))
					{
						rules.setWinCondition(new MoneyCondition(count));
						sender.sendMessage("Set win condition to: Money x" + count, true);
					}
				}
			}
			else if (args[0].equalsIgnoreCase("rentall"))
			{
				boolean rentAll = Boolean.parseBoolean(args[1]);
				rules.setDoesRentChargeAll(rentAll);
				sender.sendMessage("Rent charges all: " + rentAll, true);
			}
			else if (args[0].equalsIgnoreCase("dealbreakerdiscard"))
			{
				boolean dealBreakerDiscard = Boolean.parseBoolean(args[1]);;
				rules.setDoDealBreakersDiscardSets(dealBreakerDiscard);
				sender.sendMessage("Deal breakers discard: " + dealBreakerDiscard, true);
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
