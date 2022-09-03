package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardType;

public class ConditionAllInHand extends AbstractButtonCondition
{
	private CardType<?> type;
	
	public ConditionAllInHand(CardType<?> type)
	{
		this.type = type;
	}
	
	@Override
	public boolean evaluate(Player player, Card card)
	{
		for (Card c : player.getHand())
		{
			if (!c.getType().isRelated(type))
			{
				return false;
			}
		}
		return true;
	}
}
