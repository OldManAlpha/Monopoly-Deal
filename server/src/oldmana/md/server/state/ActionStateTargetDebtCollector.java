package oldmana.md.server.state;

import oldmana.md.server.Player;

public class ActionStateTargetDebtCollector extends ActionStateTargetPlayer
{
	public ActionStateTargetDebtCollector(Player player)
	{
		super(player);
		getServer().getGameState().setStatus(player.getName() + " used Debt Collector");
	}
	
	@Override
	public void playerSelected(Player player)
	{
		getServer().getGameState().setActionState(new ActionStateRent(getActionOwner(), player, 5));
		getActionOwner().clearRevocableCards();
	}
}
