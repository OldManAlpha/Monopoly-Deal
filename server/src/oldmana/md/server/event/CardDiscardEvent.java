package oldmana.md.server.event;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class CardDiscardEvent extends Event
{
	private Player player;
	private Card card;
	
	public CardDiscardEvent(Player player, Card card)
	{
		this.player = player;
		this.card = card;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Card getCard()
	{
		return card;
	}
}
