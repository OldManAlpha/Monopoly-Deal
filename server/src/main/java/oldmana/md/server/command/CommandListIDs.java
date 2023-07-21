package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandListIDs extends Command
{
	public CommandListIDs()
	{
		super("listids", true);
		setUsage("/listids [Collection ID]",
				"Collection ID: The ID of the collection to list the cards IDs of");
		setDescription("Lists all of the IDs of cards in the provided collection.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length < 1)
		{
			sender.sendMessage("Collection ID required.");
			return;
		}
		CardCollection collection = CardCollection.getByID(Integer.parseInt(args[0]));
		sender.sendMessage("List of card IDs in collection ID " + collection.getID() + "(Count: " + collection.getCardCount() + ")");
		for (Card card : collection.getCards())
		{
			sender.sendMessage("- " + card.getID());
		}
	}
}
