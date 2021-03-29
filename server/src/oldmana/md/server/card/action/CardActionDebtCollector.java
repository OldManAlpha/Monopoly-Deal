package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetDebtCollector;

public class CardActionDebtCollector extends CardAction
{
	public CardActionDebtCollector()
	{
		super(3, "Debt Collector");
		setDisplayName("DEBT", "COLLECTOR");
		setFontSize(6);
		setDisplayOffsetY(1);
		setDescription("Select a player and charge 5M against them.");
	}
	
	@Override
	public void playCard(Player player)
	{
		if (getServer().getPlayers().size() == 2)
		{
			player.clearRevocableCards();
			getServer().getGameState().setActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player).get(0), 5));
		}
		else
		{
			getServer().getGameState().setActionState(new ActionStateTargetDebtCollector(player));
		}
	}
}
