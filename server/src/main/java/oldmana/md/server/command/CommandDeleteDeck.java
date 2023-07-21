package oldmana.md.server.command;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.deck.CustomDeck;
import oldmana.md.server.card.collection.deck.DeckStack;

import java.io.IOException;

public class CommandDeleteDeck extends Command
{
	public CommandDeleteDeck()
	{
		super("deletedeck", true);
		setUsage("/deletedeck [Name]",
				"Name: The name of the deck to delete");
		setDescription("Deletes the deck with the provided name.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "You must specify what deck to delete.");
			return;
		}
		String name = args[0];
		Deck deck = getServer().getDeck();
		if (!deck.isDeckStackRegistered(name))
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "There is no deck by that name.");
			return;
		}
		DeckStack stack = deck.getDeckStack(name);
		if (!(stack instanceof CustomDeck))
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "That deck cannot be deleted.");
			return;
		}
		try
		{
			((CustomDeck) stack).deleteDeck();
		}
		catch (IOException e)
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_RED + e.getMessage(), true);
			e.printStackTrace();
			return;
		}
		if (deck.getDeckStack() == stack)
		{
			deck.setDeckStack("vanilla");
		}
		deck.unregisterDeckStack(name);
		sender.sendMessage(ChatColor.LIGHT_GREEN + "Successfully deleted deck '" + name + "'", true);
	}
}
