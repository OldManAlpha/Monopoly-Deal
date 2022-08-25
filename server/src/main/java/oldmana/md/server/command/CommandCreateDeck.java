package oldmana.md.server.command;

import java.io.File;
import java.io.IOException;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.card.collection.deck.CustomDeck;

public class CommandCreateDeck extends Command
{
	public CommandCreateDeck()
	{
		super("createdeck", new String[] {}, new String[] {"/createdeck"}, true);
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
		CustomDeck deck = new CustomDeck(name, getServer().getDeck().getCards());
		try
		{
			deck.writeDeck(new File("decks" + File.separator + name + ".json"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			sender.sendMessage(ChatColor.PREFIX_ALERT + "Error saving deck: " + e.getMessage());
			return;
		}
		getServer().getDeckStacks().put(name, deck);
		sender.sendMessage("Saved deck as '" + name + "'");
	}
}
