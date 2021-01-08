package oldmana.md.server.command;

import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.deck.DeckStack;

public class CommandListDecks extends Command
{
	public CommandListDecks()
	{
		super("listdecks", null, new String[] {"/listdecks"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Map<String, DeckStack> decks = getServer().getDeckStacks();
		sender.sendMessage("Available decks(" + decks.size() + "):");
		for (Entry<String, DeckStack> entry : decks.entrySet())
		{
			DeckStack stack = entry.getValue();
			sender.sendMessage(entry.getKey() + ": " + stack.getCards().size() + " Cards (" + stack.getClass().getSimpleName() + ")");
		}
	}
}
