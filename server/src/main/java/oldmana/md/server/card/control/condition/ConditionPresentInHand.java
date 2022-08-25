package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.Card.CardTypeLegacy;

public class ConditionPresentInHand extends ButtonCondition
{
	private CardTypeLegacy type;
	private boolean present;
	
	public ConditionPresentInHand(CardTypeLegacy type)
	{
		this.type = type;
		this.present = true;
	}
	
	public ConditionPresentInHand(CardTypeLegacy type, boolean present)
	{
		this.type = type;
		this.present = present;
	}
	
	@Override
	public boolean isTrue(Player player, Card card)
	{
		for (Card c : player.getHand())
		{
			if (c.getTypeLegacy() == type)
			{
				return true;
			}
		}
		return false;
	}
}
