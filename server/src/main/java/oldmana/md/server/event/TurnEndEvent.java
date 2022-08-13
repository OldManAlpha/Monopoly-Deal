package oldmana.md.server.event;

import oldmana.md.server.Player;

public class TurnEndEvent extends Event
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
