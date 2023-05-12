package oldmana.md.server.command;

import java.io.IOException;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
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
		if (getServer().getDeckStacks().containsKey(name) && !(getServer().getDeckStacks().get(name) instanceof CustomDeck))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + "That deck name is reserved.");
			return;
		}
		CustomDeck deck = new CustomDeck(name, getServer().getDeck().getCards(), getServer().getGameRules().getRootRule());
		try
		{
			deck.writeDeck();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Error saving deck: " + e.getMessage());
			return;
		}
		getServer().getDeckStacks().put(name, deck);
		getServer().getDeck().setDeckStack(deck);
		sender.sendMessage("Saved deck as '" + name + "'");
		
		if (!getServer().getDiscardPile().isEmpty() || getServer().getPlayers().stream().anyMatch(p -> !p.getAllCards().isEmpty()))
		{
			sender.sendMessage(ChatColor.PREFIX_ALERT + ChatColor.LIGHT_RED + "Warning: Cards that are not currently in the deck are not saved!");
		}
	}
}
