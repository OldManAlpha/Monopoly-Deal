package oldmana.md.server.event;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;

public class PreActionCardPlayedEvent extends CancelableEvent
{
	private Player player;
	private CardAction card;
	
	public PreActionCardPlayedEvent(Player player, CardAction card)
	{
		this.player = player;
		this.card = card;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public CardAction getCard()
	{
		return card;
	}
}
