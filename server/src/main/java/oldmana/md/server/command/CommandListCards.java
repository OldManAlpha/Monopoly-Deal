package oldmana.md.server.command;

import java.awt.Color;

import oldmana.md.server.ChatColor;
import oldmana.md.server.CommandSender;
import oldmana.md.server.MessageBuilder;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.CardCollection;

public class CommandListCards extends Command
{
	private static final String altColor = ChatColor.toChatColor(new Color(230, 230, 255));
	
	public CommandListCards()
	{
		super("listcards", true);
		setUsage("/listcards [Collection ID]",
				"Collection ID: The ID of the collection to list the cards of");
		setDescription("Lists all of the cards inside the provided collection.");
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (verifyInt(args[0]))
			{
				CardCollection collection = CardCollection.getByID(Integer.parseInt(args[0]));
				sender.sendMessage(ChatColor.LIGHT_GREEN + "End of cards in collection");
				for (int i = collection.getCardCount() - 1 ; i >= 0 ; i--)
				{
					Card card = collection.getCardAt(i);
					MessageBuilder mb = new MessageBuilder();
					mb.startHoverText(card.toString());
					mb.add((i % 2 == 0 ? altColor : ChatColor.WHITE) + "#" + (i + 1) + ": " + card.getName() +
							" (ID: " + card.getID() + ")");
					sender.sendMessage(mb.build());
				}
				sender.sendMessage(ChatColor.LIGHT_GREEN + "List of cards in collection ID " + collection.getID() + " (Count: " + collection.getCardCount() + ")");
			}
			else
			{
				sender.sendMessage("Error: Argument is not an integer.");
			}
		}
		else
		{
			sender.sendMessage("Error: Collection ID required.");
		}
	}
}
