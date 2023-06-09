package oldmana.md.server.event.state;

import oldmana.md.server.Player;
import oldmana.md.server.event.CancellableEvent;

public class TurnEndEvent extends CancellableEvent
{
	private Player player;
	
	public TurnEndEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
