package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.event.Event;

public class PlayerDrawEvent extends Event
{
	private Player player;
	
	public PlayerDrawEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
