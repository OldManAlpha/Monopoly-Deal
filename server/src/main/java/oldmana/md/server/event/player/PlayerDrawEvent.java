package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.event.Event;

import java.util.List;

/**
 * Called after a player draws cards, either at the start of their turn or when they ran out of cards.
 */
public class PlayerDrawEvent extends Event
{
	private Player player;
	private List<Card> cardsDrawn;
	private boolean extra;
	
	public PlayerDrawEvent(Player player, List<Card> cardsDrawn, boolean extra)
	{
		this.player = player;
		this.cardsDrawn = cardsDrawn;
		this.extra = extra;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public List<Card> getCardsDrawn()
	{
		return cardsDrawn;
	}
	
	public boolean isExtra()
	{
		return extra;
	}
}
