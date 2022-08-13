package oldmana.md.server.ai;

import oldmana.md.server.Player;
import oldmana.md.server.state.ActionState;
import oldmana.md.server.state.ActionStateDraw;
import oldmana.md.server.state.ActionStatePlay;

public class RandomAI extends PlayerAI
{
	public RandomAI(Player player)
	{
		super(player);
	}
	
	@Override
	public void doAction()
	{
		Player player = getPlayer();
		ActionState state = getServer().getGameState().getActionState();
		if (state.getActionOwner() == player)
		{
			if (state instanceof ActionStateDraw)
			{
				player.draw();
			}
			else if (state instanceof ActionStatePlay)
			{
				
			}
		}
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
