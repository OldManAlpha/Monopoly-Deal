package oldmana.md.server.status;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.event.MDEventListener;

/**
 * Status Effects are automatically registered for events when applied to a player and automatically unregistered when removed from a player
 *
 */
public class StatusEffect implements MDEventListener
{
	private Player player;
	
	public StatusEffect(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
