package oldmana.md.server.command;

import java.io.File;

import oldmana.md.server.CommandSender;
import oldmana.md.server.card.CardProperty;
import oldmana.md.server.card.CardProperty.PropertyColor;
import oldmana.md.server.card.action.CardActionDealBreaker;
import oldmana.md.server.card.action.CardActionPassGo;
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
		CustomDeck deck = new CustomDeck("customdeck");
		deck.addCard(new CardActionDealBreaker());
		deck.addCard(new CardActionPassGo());
		deck.addCard(new CardProperty(new PropertyColor[] {PropertyColor.BROWN, PropertyColor.LIGHT_BLUE, PropertyColor.UTILITY}, 6, "OP Property", true));
		getServer().getDeckStacks().put("customdeck", deck);
		deck.writeDeck(new File("decks" + File.separator + "customdeck.json"));
	}
}
