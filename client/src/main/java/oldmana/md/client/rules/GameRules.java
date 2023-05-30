package oldmana.md.client.rules;

import org.json.JSONObject;

public class GameRules
{
	private int maxCardsInHand = 7;
	private int maxMoves = 3;
	private boolean canDiscardEarly = false;
	
	public int getMaxCardsInHand()
	{
		return maxCardsInHand;
	}
	
	public int getMaxMoves()
	{
		return maxMoves;
	}
	
	public boolean canDiscardEarly()
	{
		return canDiscardEarly;
	}
	
	public void applyGameRules(JSONObject rules)
	{
		maxCardsInHand = rules.getInt("maxCardsInHand");
		maxMoves = rules.getInt("maxMoves");
		canDiscardEarly = rules.getBoolean("canDiscardEarly");
	}
}
