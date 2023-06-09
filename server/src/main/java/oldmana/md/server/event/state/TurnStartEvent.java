package oldmana.md.server.event.state;

import oldmana.md.server.Player;
import oldmana.md.server.event.Event;

public class TurnStartEvent extends Event
{
	private Player player;
	
	public TurnStartEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
