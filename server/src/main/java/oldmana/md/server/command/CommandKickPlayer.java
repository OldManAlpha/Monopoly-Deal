package oldmana.md.server.command;

import oldmana.md.server.CommandSender;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.Deck;
import oldmana.md.server.state.GameState;

public class CommandKickPlayer extends Command
{
	public CommandKickPlayer()
	{
		super("kickplayer", new String[] {"kick"}, new String[] {"/kick [Player ID] <Reason>"}, true);
	}
	
	@Override
	public void executeCommand(CommandSender sender, String[] args)
	{
		Player player = getServer().getPlayerByID(Integer.parseInt(args[0]));
		Deck deck = getServer().getDeck();
		for (Card card : player.getAllCards())
		{
			card.transfer(deck, -1, 0.2);
		}
		deck.shuffle();
		GameState gs = getServer().getGameState();
		if (gs.getActivePlayer() == player)
		{
			gs.nextTurn();
		}
		String reason = "Kicked by operator";
		if (args.length > 1)
		{
			reason = getFullStringArgument(args, 1);
		}
		sender.sendMessage("Kicked player " + player.getName() + " (ID: " + player.getID() + ") for '" + reason + "'", true);
		getServer().kickPlayer(player, reason);
	}
}
