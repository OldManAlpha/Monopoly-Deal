package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.state.ActionStateListener;
import oldmana.general.md.server.state.ActionStateRent;
import oldmana.general.md.server.state.ActionStateTargetPlayer;

public class CardActionDoubleTheRent extends CardAction
{
	private ActionStateTargetPlayer targetAction;
	private ActionStateRent rent;
	
	public CardActionDoubleTheRent()
	{
		super(1, "Double The Rent");
		setDisplayName("DOUBLE", "THE RENT!");
		setFontSize(7);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public void playCard(Player player)
	{
		//targetAction = new ActionStateTargetPlayer(getOwner());
		//targetAction.setListener(this);
		//getServer().setActionState(targetAction);
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
