package oldmana.md.server.rules;

import oldmana.md.server.rules.win.WinCondition;

public class GameRules
{
	private WinCondition winCondition;
	private boolean rentChargesAll = true;
	private boolean dealBreakersDiscardSets = false;
	
	public WinCondition getWinCondition()
	{
		return winCondition;
	}
	
	public void setWinCondition(WinCondition condition)
	{
		winCondition = condition;
	}
	
	public boolean doesRentChargeAll()
	{
		return rentChargesAll;
	}
	
	public void setDoesRentChargeAll(boolean rentChargesAll)
	{
		this.rentChargesAll = rentChargesAll;
	}
	
	public boolean doDealBreakersDiscardSets()
	{
		return dealBreakersDiscardSets;
	}
	
	public void setDoDealBreakersDiscardSets(boolean discard)
	{
		dealBreakersDiscardSets = discard;
	}
}
