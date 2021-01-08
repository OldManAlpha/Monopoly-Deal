package oldmana.md.server.state;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public class ActionStateTargetSlyDeal extends ActionStateTargetPlayerProperty
{
	public ActionStateTargetSlyDeal(Player player)
	{
		super(player);
		getServer().getGameState().setStatus(player.getName() + " used Sly Deal");
	}
	
	@Override
	public void onCardSelected(CardProperty card)
	{
		getActionOwner().clearRevokableCards();
		getServer().getGameState().setCurrentActionState(new ActionStateStealProperty(getActionOwner(), card));
	}
}
