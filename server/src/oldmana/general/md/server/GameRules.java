package oldmana.general.md.server;

public class GameRules
{
	private int monopolyWinCount = 3;
	private boolean rentChargesAll = true;
	private boolean dealBreakersDiscardSets = false;
	
	public int getMonopoliesRequiredToWin()
	{
		return monopolyWinCount;
	}
	
	public void setMonopoliesRequiredToWin(int monopolies)
	{
		monopolyWinCount = monopolies;
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
