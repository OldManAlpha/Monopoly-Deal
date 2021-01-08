package oldmana.md.server.state;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardProperty;

public class ActionStateTargetForcedDeal extends ActionStateTargetSelfPlayerProperty
{
	public ActionStateTargetForcedDeal(Player player)
	{
		super(player);
		getServer().getGameState().setStatus(player.getName() + " used Forced Deal");
	}
	
	public void onCardsSelected(CardProperty self, CardProperty other)
	{
		getActionOwner().clearRevokableCards();
		getServer().getGameState().setCurrentActionState(new ActionStateTradeProperties(self, other));
	}
}
