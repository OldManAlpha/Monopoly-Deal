package oldmana.md.server.event;

import oldmana.md.server.Player;

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
