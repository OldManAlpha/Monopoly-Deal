package oldmana.md.server.event;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.Player;

public class PlayersWonEvent extends CancelableEvent
{
	private List<Player> players;
	
	private int deferredTurns;
	
	public PlayersWonEvent(List<Player> players)
	{
		this.players = players;
	}
	
	public List<Player> getWinners()
	{
		return new ArrayList<Player>(players);
	}
	
	public void setWinners(List<Player> players)
	{
		this.players = new ArrayList<Player>(players);
	}
	
	public void setWinner(Player player)
	{
		players = new ArrayList<Player>();
		players.add(player);
	}
	
	public void setWinDeferredTurns(int turns)
	{
		deferredTurns = turns;
	}
	
	public int getWinDeferredTurns()
	{
		return deferredTurns;
	}
}
