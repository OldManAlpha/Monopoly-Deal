package oldmana.md.server.ai;

import oldmana.md.server.Player;

public class NormalAI extends PlayerAI
{
	public NormalAI(Player player)
	{
		super(player);
	}
	
	@Override
	public void doAction()
	{
		Player player = getPlayer();
		int turns = getServer().getGameState().getTurnsRemaining();
		// Check if winning is possible with properties in hand
		// Else: Check if winning is possible with trading cards
		// Else: Check if winning is possible with rent
		// Else: Verify financial security
		// Else: Prevent players from winning
		// Else: Do normal things
	}
	
	@Override
	public double getWinThreat(Player player)
	{
		
		return 0;
	}
	
	@Override
	public double getRentThreat(Player player)
	{
		return 0;
	}
}
