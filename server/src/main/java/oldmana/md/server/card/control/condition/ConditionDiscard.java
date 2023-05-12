package oldmana.md.server.card.control.condition;

import oldmana.md.server.card.control.condition.logic.ConditionAnd;
import oldmana.md.server.card.control.condition.logic.ConditionNot;
import oldmana.md.server.card.control.condition.logic.ConditionOr;
import oldmana.md.server.card.CardType;

public class ConditionDiscard extends ConditionAnd
{
	public ConditionDiscard()
	{
		//super(new ConditionState(ActionStateDiscard.class),
		//		new ConditionOwnTurn(),
		//		new ConditionOr(
		//				new ConditionNot(new ConditionIsType(CardType.PROPERTY)),
		//				new ConditionAllInHand(CardType.PROPERTY)));
	}
}
