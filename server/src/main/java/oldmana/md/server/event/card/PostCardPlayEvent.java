package oldmana.md.server.event.card;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.event.Event;

/**
 * Called after the card has been played.
 */
public class PostCardPlayEvent extends Event
{
	private Player player;
	private Card card;
	private PlayArguments arguments;
	
	public PostCardPlayEvent(Player player, Card card, PlayArguments arguments)
	{
		this.player = player;
		this.card = card;
		this.arguments = arguments;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public PlayArguments getArguments()
	{
		return arguments;
	}
}
