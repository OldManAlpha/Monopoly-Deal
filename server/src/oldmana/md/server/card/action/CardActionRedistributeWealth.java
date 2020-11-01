package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateRent;

public class CardActionRedistributeWealth extends CardAction
{
	public CardActionRedistributeWealth()
	{
		super(5, "Redistribute Wealth");
		setDisplayName("REDISTRIBUTE", "WEALTH");
		setFontSize(5);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		Player oligarch = null;
		int oligarchValue = 0;
		for (Player p : getServer().getPlayersExcluding(player))
		{
			int bankValue = p.getBank().getTotalValue();
			if (bankValue > oligarchValue)
			{
				oligarch = p;
				oligarchValue = bankValue;
			}
		}
		getServer().getGameState().setCurrentActionState(new ActionStateRent(player, oligarch, (int) Math.ceil(oligarchValue * 0.4)));
	}
	
	@Override
	public boolean canPlayCard(Player player)
	{
		int bankValue = player.getBank().getTotalValue();
		for (Player p : getServer().getPlayersExcluding(player))
		{
			if (p.getBank().getTotalValue() > bankValue)
			{
				return true;
			}
		}
		return false;
	}
}
