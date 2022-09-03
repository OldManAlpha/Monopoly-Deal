package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardType;

public class ConditionPresentInHand extends AbstractButtonCondition
{
	private CardType<?> type;
	private boolean present;
	
	public ConditionPresentInHand(CardType<?> type)
	{
		this.type = type;
		this.present = true;
	}
	
	public ConditionPresentInHand(CardType<?> type, boolean present)
	{
		this.type = type;
		this.present = present;
	}
	
	@Override
	public boolean evaluate(Player player, Card card)
	{
		for (Card c : player.getHand())
		{
			if (c.getType() == type)
			{
				return present;
			}
		}
		return !present;
	}
}
