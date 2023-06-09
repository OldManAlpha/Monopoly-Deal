package oldmana.md.server.event.card;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.play.PlayArguments;
import oldmana.md.server.event.CancellableEvent;

/**
 * Called before the card is played.
 */
public class CardPlayEvent extends CancellableEvent
{
	private Player player;
	private Card card;
	private PlayArguments arguments;
	
	public CardPlayEvent(Player player, Card card, PlayArguments arguments)
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
