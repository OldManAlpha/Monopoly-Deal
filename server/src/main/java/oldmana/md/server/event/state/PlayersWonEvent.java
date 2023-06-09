package oldmana.md.server.event.state;

import java.util.ArrayList;
import java.util.List;

import oldmana.md.server.Player;
import oldmana.md.server.event.CancellableEvent;

public class PlayersWonEvent extends CancellableEvent
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
	
	public int getWinDeferredTurns()
	{
		return deferredTurns;
	}
	
	public void setWinDeferredTurns(int turns)
	{
		deferredTurns = turns;
	}
}
