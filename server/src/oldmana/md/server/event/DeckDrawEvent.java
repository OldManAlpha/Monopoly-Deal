package oldmana.md.server.event;

import oldmana.md.server.Player;

public class DeckDrawEvent extends Event
{
	private Player player;
	
	public DeckDrawEvent(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
