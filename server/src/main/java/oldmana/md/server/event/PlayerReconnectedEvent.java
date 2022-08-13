package oldmana.md.server.event;

import oldmana.md.server.Player;

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
