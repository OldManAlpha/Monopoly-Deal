package oldmana.md.server.command;

import java.io.IOException;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.deck.CustomDeck;

public class CommandCreateDeck extends Command
{
	public CommandCreateDeck()
	{
		super("createdeck", new String[] {"savedeck"}, new String[] {"/createdeck"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage("You must specify a name for the deck.");
			return;
		}
		String name = args[0];
		Deck deck = getServer().getDeck();
		if (deck.isDeckStackRegistered(name) && !(deck.getDeckStack(name) instanceof CustomDeck))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "That deck name is reserved.");
			return;
		}
		CustomDeck stack = new CustomDeck(name, getServer().getDeck().getCards(), getServer().getGameRules().getRootRule());
		try
		{
			stack.writeDeck();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Error saving deck: " + e.getMessage());
			return;
		}
		deck.registerDeckStack(name, stack);
		getServer().getDeck().setDeckStack(stack);
		sender.sendMessage("Saved deck as '" + name + "'");
		
		if (!getServer().getDiscardPile().isEmpty() || getServer().getPlayers().stream().anyMatch(p -> !p.getAllCards().isEmpty()))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_RED + "Warning: Cards that are not currently in the deck are not saved!");
		}
	}
}
