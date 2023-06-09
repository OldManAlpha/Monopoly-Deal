package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.event.Event;

import java.util.List;

public class PlayerDrawEvent extends Event
{
	private Player player;
	private List<Card> cardsDrawn;
	
	public PlayerDrawEvent(Player player, List<Card> cardsDrawn)
	{
		this.player = player;
		this.cardsDrawn = cardsDrawn;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public List<Card> getCardsDrawn()
	{
		return cardsDrawn;
	}
}
