package oldmana.md.server.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oldmana.md.net.packet.server.PacketStatus;
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
		server.broadcastPacket(new PacketStatus(""));
		server.getGameState().endGame();
		Deck deck = server.getDeck();
		for (Player player : server.getPlayers())
		{
			for (Card card : player.getBank().getCardsInReverse())
			{
				card.transfer(deck, -1, 4);
			}
			List<PropertySet> sets = new ArrayList<PropertySet>(player.getPropertySets());
			Collections.reverse(sets);
			for (PropertySet set : sets)
			{
				for (Card card : set.getCardsInReverse())
				{
					card.transfer(deck, -1, 4);
				}
			}
			for (Card card : player.getHand().getCardsInReverse())
			{
				card.transfer(deck, -1, 4);
			}
		}
		for (Card card : server.getDiscardPile().getCardsInReverse())
		{
			card.transfer(deck, -1, 4);
		}
		deck.shuffle();
	}
}
