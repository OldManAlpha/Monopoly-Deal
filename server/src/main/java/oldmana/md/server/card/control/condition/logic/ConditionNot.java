package oldmana.md.server.card.control.condition.logic;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.control.condition.AbstractButtonCondition;

public class ConditionNot extends AbstractButtonCondition
{
	private AbstractButtonCondition condition;
	
	public ConditionNot(AbstractButtonCondition condition)
	{
		this.condition = condition;
	}
	
	@Override
	public boolean evaluate(Player player, Card card)
	{
		return !condition.evaluate(player, card);
	}
}
