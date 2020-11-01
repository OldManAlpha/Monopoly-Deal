package oldmana.general.md.server;

import java.util.HashMap;
import java.util.Map;

public class PlayerRegistry
{
	private Map<Integer, String> players = new HashMap<Integer, String>();
	
	public PlayerRegistry()
	{
		players.put(1, "Player 1");
		players.put(2, "Player 2");
		players.put(3, "Player 3");
		players.put(4, "Player 4");
		players.put(5, "Player 5");
		players.put(6, "Player 6");
		players.put(7, "Player 7");
	}
	
	public boolean containsID(int id)
	{
		return players.containsKey(id);
	}
	
	public String getNameOf(int id)
	{
		return players.get(id);
	}
}
