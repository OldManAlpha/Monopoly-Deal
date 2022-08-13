package oldmana.md.server.event;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;

public class PostActionCardPlayedEvent extends Event
{
	private Player player;
	private CardAction card;
	
	public PostActionCardPlayedEvent(Player player, CardAction card)
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
