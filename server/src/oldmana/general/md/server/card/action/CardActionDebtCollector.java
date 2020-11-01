package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.state.ActionStateListener;
import oldmana.general.md.server.state.ActionStateRent;
import oldmana.general.md.server.state.ActionStateTargetDebtCollector;
import oldmana.general.md.server.state.ActionStateTargetPlayer;

public class CardActionDebtCollector extends CardAction
{
	private ActionStateTargetPlayer targetAction;
	private ActionStateRent rent;
	
	public CardActionDebtCollector()
	{
		super(3, "Debt Collector");
		setDisplayName("DEBT", "COLLECTOR");
		setFontSize(6);
		setDisplayOffsetY(1);
		//setRevocable(false);
		//setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public void playCard(Player player)
	{
		//targetAction = new ActionStateTargetPlayer(getOwner());
		//targetAction.setListener(this);
		//getServer().setActionState(targetAction);
		if (getServer().getPlayers().size() == 2)
		{
			player.clearRevokableCards();
			getServer().getGameState().setCurrentActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player).get(0), 5));
		}
		else
		{
			getServer().getGameState().setCurrentActionState(new ActionStateTargetDebtCollector(player));
		}
	}
	
	/*
	@Override
	public void selectPlayer(Player player)
	{
		player.setActionRequest(new ActionRequestPay(getOwner(), 5));
	}
	*/

	/*
	@Override
	public boolean onActionStateUpdate()
	{
		if (rent == null)
		{
			rent = new ActionStateRent(getOwner(), targetAction.getTarget(), 5);
			rent.setListener(this);
			getServer().setActionState(rent);
		}
		else
		{
			//if (rent.getActionTargets().size() == rent.getNumberOfPaid() + rent.getNumberOfAcceptedRefusals())
			{
				getServer().getGameState().nextNaturalActionState();
			}
		}
		return true;
	}
	*/
}
