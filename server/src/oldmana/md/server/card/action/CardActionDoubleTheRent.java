package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateListener;
import oldmana.md.server.state.ActionStateRent;
import oldmana.md.server.state.ActionStateTargetPlayer;

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
