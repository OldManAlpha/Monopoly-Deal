package oldmana.md.server.command;

import java.util.ArrayList;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandTransferAll extends Command
{
	public CommandTransferAll()
	{
		super("transferall", true);
		setUsage("/transferall [From Collection] [To Collection] <Transfer Time>",
				"From Collection: The ID of the collection to transfer cards from",
				"To Collection: The ID of the collection to transfer cards to",
				"Transfer Time (Optional): The number of seconds it takes to transfer each card");
		setDescription("Transfers all cards from one collection to another.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		CardCollection from = CardCollection.getByID(Integer.parseInt(args[0]));
		CardCollection to = CardCollection.getByID(Integer.parseInt(args[1]));
		double time = Math.min(1, Math.max(2 / (double) Math.max(from.getCardCount(), 1), 0.1));
		if (args.length >= 3)
		{
			time = Double.parseDouble(args[2]);
		}
		for (Card card : new ArrayList<Card>(from.getCards()))
		{
			card.transfer(to, -1, time);
		}
		sender.sendMessage("Transferred the cards in collection ID " + from.getID() + " to collection ID " + to.getID());
	}
}
