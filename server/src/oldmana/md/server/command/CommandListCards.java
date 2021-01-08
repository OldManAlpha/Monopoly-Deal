package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandListCards extends Command
{
	public CommandListCards()
	{
		super("listcards", null, new String[] {"/listcards [Collection ID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (verifyInt(args[0]))
			{
				CardCollection collection = CardCollection.getCardCollection(Integer.parseInt(args[0]));
				sender.sendMessage("List of cards in collection ID " + collection.getID() + "(Count: " + collection.getCardCount() + ")");
				for (Card card : collection.getCards())
				{
					sender.sendMessage("- " + card.getID() + ": " + card.toString());
				}
			}
			else
			{
				sender.sendMessage("Error: Argument is not an integer.");
			}
		}
		else
		{
			sender.sendMessage("Error: Collection ID required.");
		}
	}
}
