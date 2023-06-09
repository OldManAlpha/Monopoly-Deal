package oldmana.md.server.event.player;

import oldmana.md.server.Player;
import oldmana.md.server.event.Event;

/**
 * Called after the player has been removed from the server but before the clients are notified.
 */
public class PlayerRemovedEvent extends Event
{
	private Player player;
	
	public PlayerRemovedEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
