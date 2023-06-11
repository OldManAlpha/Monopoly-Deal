package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.event.CancellableEvent;

public class PlayerPreDrawEvent extends CancellableEvent
{
	private Player player;
	private int cardsToDraw;
	private boolean extra;
	
	public PlayerPreDrawEvent(Player player, int cardsToDraw, boolean extra)
	{
		this.player = player;
		this.cardsToDraw = cardsToDraw;
		this.extra = extra;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public int getCardsToDraw()
	{
		return cardsToDraw;
	}
	
	public boolean isExtra()
	{
		return extra;
	}
}
