package oldmana.md.server.command;

import java.util.Map;
import java.util.Map.Entry;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.card.collection.deck.DeckStack;

public class CommandListDecks extends Command
{
	public CommandListDecks()
	{
		super("listdecks", true);
		setDescription("Lists all of the available decks.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Map<String, DeckStack> decks = getServer().getDeck().getDeckStacks();
		sender.sendMessage("Available decks(" + decks.size() + "):");
		for (Entry<String, DeckStack> entry : decks.entrySet())
		{
			String deckName = entry.getKey();
			DeckStack stack = entry.getValue();
			boolean inUse = getServer().getDeck().getDeckStack() == stack;
			String txt = deckName + ": " + stack.getCards().size() + " Cards (" +
					stack.getClass().getSimpleName() + ")";
			MessageBuilder message = new MessageBuilder();
			if (inUse)
			{
				message.add(ChatColor.LIGHT_YELLOW + txt + " (In Use)");
			}
			else
			{
				message.startHoverText("Switch to " + deckName + " deck");
				message.addCommand(ChatColor.LINK + txt, "setdeck " + deckName);
			}
			sender.sendMessage(message.build());
		}
	}
}
