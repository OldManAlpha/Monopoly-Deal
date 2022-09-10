package oldmana.md.server.command;

import java.util.ArrayList;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandTransferAll extends Command
{
	public CommandTransferAll()
	{
		super("transferall", null, new String[] {"/transferall [From Collection] [To Collection] <Transfer Time>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		CardCollection from = CardCollection.getCardCollection(Integer.parseInt(args[0]));
		CardCollection to = CardCollection.getCardCollection(Integer.parseInt(args[1]));
		double time = 1;
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
