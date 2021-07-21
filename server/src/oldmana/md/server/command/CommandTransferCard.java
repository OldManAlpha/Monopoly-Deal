package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.collection.CardCollection;
import oldmana.md.server.card.collection.PropertySet;

public class CommandTransferCard extends Command
{
	public CommandTransferCard()
	{
		super("transfercard", new String[] {"transfer"}, new String[] {"/transfercard [Card ID] [Set ID] <Index> <Speed>", 
				"/transfercard [Set ID] [-1] [Player ID] <Speed>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		//                    Card ID  Index
		//                       \/     \/
		// Example: transfercard 107 2 [0]
		//                          /\
		//                        Set ID
		
		// Creating new property sets:
		//                    Card ID Player ID
		//                       \/     \/
		// Example: transfercard 107 -1 2
		//                           /\
		//                   -1 indicates new set
		if (args.length >= 2)
		{
			double speed = 1;
			if (args.length >= 4 && verifyDouble(args[3]))
			{
				speed = parseDouble(args[3]);
			}
			Card card = Card.getCard(Integer.parseInt(args[0]));
			int setId = Integer.parseInt(args[1]);
			if (setId > -1)
			{
				CardCollection collection = CardCollection.getCardCollection(setId);
				if (!(collection instanceof PropertySet) || card instanceof CardProperty)
				{
					int index = -1;
					if (args.length >= 3)
					{
						index = Integer.parseInt(args[2]);
					}
					card.transfer(collection, index, speed);
					sender.sendMessage("Transferred card");
				}
				else
				{
					sender.sendMessage("Card of type " + card.getClass().getSimpleName() + " cannot be inserted into a PropertySet");
				}
			}
			else
			{
				if (card instanceof CardProperty)
				{
					if (args.length >= 3)
					{
						Player owner = getServer().getPlayerByID(Integer.parseInt(args[2]));
						PropertySet set = owner.createPropertySet();
						card.transfer(set, -1, speed);
						sender.sendMessage("Created new set and transferred card");
					}
				}
				else
				{
					sender.sendMessage("Card of type " + card.getClass().getSimpleName() + " cannot be inserted into a PropertySet");
				}
			}
		}
	}
}
