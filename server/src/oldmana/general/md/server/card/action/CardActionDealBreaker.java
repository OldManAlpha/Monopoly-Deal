package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.state.ActionStateTargetPlayerMonopoly;

public class CardActionDealBreaker extends CardAction
{
	public CardActionDealBreaker()
	{
		super(5, "Deal Breaker");
		setDisplayName("DEAL", "BREAKER");
		setFontSize(7);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateTargetPlayerMonopoly(player));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		for (Player other : getServer().getPlayersExcluding(player))
		{
			if (other.getMonopolyCount() > 0)
			{
				return true;
			}
		}
		return false;
	}
}
