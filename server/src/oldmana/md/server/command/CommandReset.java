package oldmana.md.server.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.md.server.CommandSender;
import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.card.collection.PropertySet;

public class CommandReset extends Command
{
	public CommandReset()
	{
		super("reset", null, new String[] {"/reset"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		MDServer server = getServer();
		server.getGameState().endGame();
		Deck deck = server.getDeck();
		for (Player player : server.getPlayers())
		{
			for (Card card : player.getBank().getCardsInReverse())
			{
				putCardAway(card);
			}
			List<PropertySet> sets = new ArrayList<PropertySet>(player.getPropertySets());
			Collections.reverse(sets);
			for (PropertySet set : sets)
			{
				for (Card card : set.getCardsInReverse())
				{
					putCardAway(card);
				}
			}
			for (Card card : player.getHand().getCardsInReverse())
			{
				putCardAway(card);
			}
		}
		for (Card card : server.getDiscardPile().getCardsInReverse())
		{
			putCardAway(card);
		}
		
		for (Card card : deck.getCards(true))
		{
			if (!deck.getDeckStack().hasCard(card))
			{
				putCardAway(card);
			}
		}
		
		for (Card card : deck.getDeckStack().getCards())
		{
			if (!deck.hasCard(card))
			{
				putCardAway(card);
			}
		}
		
		deck.shuffle();
		
		sender.sendMessage("The game has been reset", true);
	}
	
	public void putCardAway(Card card)
	{
		Deck deck = getServer().getDeck();
		if (deck.getDeckStack().hasCard(card))
		{
			card.transfer(deck, -1, 4);
		}
		else
		{
			card.transfer(getServer().getVoidCollection(), -1, 4);
		}
	}
}
