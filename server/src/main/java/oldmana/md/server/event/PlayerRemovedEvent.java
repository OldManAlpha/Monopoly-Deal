package oldmana.md.server.event;

import oldmana.md.server.Player;

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
