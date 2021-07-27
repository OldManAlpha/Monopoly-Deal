package oldmana.md.server.event;

import oldmana.md.server.Player;

public class PlayerJoinedEvent extends Event
{
	private Player player;
	
	public PlayerJoinedEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
