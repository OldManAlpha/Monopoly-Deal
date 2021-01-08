package oldmana.md.server.command;

import java.util.Map;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.deck.DeckStack;

public class CommandSetDeck extends Command
{
	public CommandSetDeck()
	{
		super("setdeck", null, new String[] {"/setdeck [Deck Name]"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			String name = args[0];
			Map<String, DeckStack> decks = getServer().getDeckStacks();
			if (decks.containsKey(name))
			{
				getServer().getDeck().setDeckStack(decks.get(name));
				sender.sendMessage("Deck swapped");
			}
			else
			{
				sender.sendMessage("There's no deck by the name '" + name + "'");
			}
		}
		else
		{
			sender.sendMessage("Deck name required.");
		}
	}
}
