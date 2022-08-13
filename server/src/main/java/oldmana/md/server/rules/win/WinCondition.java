package oldmana.md.server.rules.win;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;

public abstract class WinCondition
{
	public abstract boolean isWinner(Player player);
	
	public List<Player> getWinners()
	{
		List<Player> winners = new ArrayList<Player>();
		for (Player player : getServer().getPlayers())
		{
			if (isWinner(player))
			{
				winners.add(player);
			}
		}
		return winners;
	}
	
	public MDServer getServer()
	{
		return MDServer.getInstance();
	}
}
