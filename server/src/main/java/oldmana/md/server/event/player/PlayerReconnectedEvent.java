package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.event.Event;

public class PlayerReconnectedEvent extends Event
{
	private Player player;
	
	public PlayerReconnectedEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
