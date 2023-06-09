package oldmana.md.server.card.play.argument;

import oldmana.md.server.Player;
import oldmana.md.server.card.play.PlayArgument;

public class PlayerArgument implements PlayArgument
{
	private Player player;
	
	public PlayerArgument(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
