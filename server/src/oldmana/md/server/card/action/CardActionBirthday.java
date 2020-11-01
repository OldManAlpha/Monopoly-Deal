package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateListener;
import oldmana.md.server.state.ActionStateRent;

public class CardActionBirthday extends CardAction implements ActionStateListener
{
	public CardActionBirthday()
	{
		super(2, "It's My Birthday");
		setDisplayName("IT'S MY", "BIRTHDAY");
		setFontSize(7);
		setRevocable(false);
		setMarksPreviousUnrevocable(true);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), 2));
	}
	
	@Override
	public boolean onActionStateUpdate()
	{
		//if (rent.getActionTargets().size() == rent.getNumberOfPaid() + rent.getNumberOfAcceptedRefusals())
		{
			getServer().getGameState().nextNaturalActionState();
		}
		return true;
	}
}
