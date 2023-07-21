package oldmana.md.server.command;

import java.io.IOException;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.deck.CustomDeck;

public class CommandSaveDeck extends Command
{
	public CommandSaveDeck()
	{
		super("savedeck", true);
		setAliases("createdeck");
		setUsage("/savedeck [Name]",
				"Name: The name to save the deck as");
		setDescription("Saves the game rules and cards in the deck to the disk.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "You must specify a name for the deck.");
			return;
		}
		String name = args[0];
		Deck deck = getServer().getDeck();
		if (deck.isDeckStackRegistered(name) && !(deck.getDeckStack(name) instanceof CustomDeck))
		{
			sender.sendMessage(ChatColor.LIGHT_RED + "That deck name is reserved.");
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
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_RED + "Error saving deck: " + e.getMessage());
			return;
		}
		deck.registerDeckStack(name, stack);
		getServer().getDeck().setDeckStack(stack);
		sender.sendMessage(ChatColor.LIGHT_GREEN + "Saved deck as '" + name + "'");
		
		if (!getServer().getDiscardPile().isEmpty() || getServer().getPlayers().stream().anyMatch(p -> !p.getAllCards().isEmpty()))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_RED +
					"Warning: Cards that are not currently in the deck are not saved!");
		}
	}
}
