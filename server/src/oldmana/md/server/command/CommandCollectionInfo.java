package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.CardCollection;

public class CommandCollectionInfo extends Command
{
	public CommandCollectionInfo()
	{
		super("collectioninfo", null, new String[] {"/collectioninfo [Collection ID]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			CardCollection collection = CardCollection.getCardCollection(Integer.parseInt(args[0]));
			sender.sendMessage("Collection ID " + collection.getID() + " is a " + collection.getClass().getSimpleName() + " with a card count of " + 
			collection.getCardCount());
		}
		else
		{
			sender.sendMessage("Collection ID required.");
		}
	}
}
