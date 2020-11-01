package oldmana.md.server;

import java.util.HashMap;
import java.util.Map;

public class PlayerRegistry
{
	private Map<Integer, String> players = new HashMap<Integer, String>();
	
	public PlayerRegistry()
	{
		//players.put(1, "Oldmana");
		//players.put(2, "Biliam");
		//players.put(3, "Johny Muffin");
		players.put(1, "Player 1");
		players.put(2, "Player 2");
		players.put(3, "Player 3");
		players.put(4, "Player 4");
		players.put(5, "Player 5");
		players.put(6, "Player 6");
		players.put(7, "Player 7");
		
		players.put(865777, "Oldmana");
		
		players.put(580924, "drace9");
		players.put(144710, "caseygamer01234");
		players.put(939426, "icedragon420");
		
		players.put(744178, "Biliam");
		players.put(677827, "Zyga");
		players.put(221756, "Johny Muffin");
		players.put(124318, "AndrewKart");
		
		players.put(788032, "Depwession");
		players.put(85588, "Anxwiety");
		
		players.put(108020, "Stezy");
		
		players.put(276100, "SorrowfulExistence");
		
		players.put(5000, "Brett");
		players.put(600, "Dad");
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
