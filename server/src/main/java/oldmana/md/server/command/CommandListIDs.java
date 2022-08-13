package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandListIDs extends Command
{
	public CommandListIDs()
	{
		super("listids", null, new String[] {"/listids [Collection ID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			CardCollection collection = CardCollection.getCardCollection(Integer.parseInt(args[0]));
			sender.sendMessage("List of card IDs in collection ID " + collection.getID() + "(Count: " + collection.getCardCount() + ")");
			for (Card card : collection.getCards())
			{
				sender.sendMessage("- " + card.getID());
			}
		}
		else
		{
			sender.sendMessage("Collection ID required.");
		}
	}
}
