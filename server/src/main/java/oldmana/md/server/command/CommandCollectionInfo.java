package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.CardCollection;

public class CommandCollectionInfo extends Command
{
	public CommandCollectionInfo()
	{
		super("collectioninfo", true);
		setUsage("/collectioninfo [Collection ID]",
				"Collection ID: The ID of the collection to see info on");
		setDescription("Checks what the type of collection with the provided ID is and displays how many cards are in it.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			CardCollection collection = CardCollection.getByID(Integer.parseInt(args[0]));
			sender.sendMessage("Collection ID " + collection.getID() + " is a " + collection.getClass().getSimpleName() + " with a card count of " + 
			collection.getCardCount());
		}
		else
		{
			sender.sendMessage("Collection ID required.");
		}
	}
}
