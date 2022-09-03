package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardAction;

public class ConditionCanPlayCard extends AbstractButtonCondition
{
	@Override
	public boolean evaluate(Player player, Card card)
	{
		if (card instanceof CardAction)
		{
			return ((CardAction) card).canPlayCard(player);
		}
		return true;
	}
}
