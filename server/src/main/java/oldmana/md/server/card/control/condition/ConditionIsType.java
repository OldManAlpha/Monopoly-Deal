package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardType;

public class ConditionIsType extends AbstractButtonCondition
{
	private CardType<?> type;
	
	public ConditionIsType(CardType<?> type)
	{
		this.type = type;
	}
	
	@Override
	public boolean evaluate(Player player, Card card)
	{
		return card.getType() == type;
	}
}
