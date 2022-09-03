package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

public class ConditionOwnTurn extends AbstractButtonCondition
{
	@Override
	public boolean evaluate(Player player, Card card)
	{
		return getServer().getGameState().getActivePlayer() == player;
	}
}
