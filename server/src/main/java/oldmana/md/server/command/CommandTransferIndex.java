package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.CardCollection;

public class CommandTransferIndex extends Command
{
	public CommandTransferIndex()
	{
		super("transferindex", null, new String[] {"/transferindex [From ID] [From Index] [To ID] <To Index> <Time>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		//                    From ID To ID
		//                       \/   \/
		// Example: transferindex 1 0 2 [0] <- To Index
		//                          /\
		//                       From Index
		if (args.length >= 3)
		{
			double time = 1;
			if (args.length >= 4 && verifyDouble(args[3]))
			{
				time = parseDouble(args[3]);
			}
			
			CardCollection from = CardCollection.getByID(Integer.parseInt(args[0]));
			CardCollection to = CardCollection.getByID(Integer.parseInt(args[2]));
			int fromIndex = Integer.parseInt(args[1]);
			int toIndex = args.length >= 4 ? Integer.parseInt(args[3]) : -1;
			from.getCardAt(fromIndex).transfer(to, toIndex, time);
			sender.sendMessage("Transferred card from collection " + from.getID() + " at index " + fromIndex + " to collection " + to.getID() + 
					" at index " + toIndex);
		}
		else
		{
			sender.sendMessage("Not enough arguments!");
			sendUsage(sender);
		}
	}
}
