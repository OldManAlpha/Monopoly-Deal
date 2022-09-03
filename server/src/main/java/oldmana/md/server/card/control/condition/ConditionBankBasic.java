package oldmana.md.server.card.control.condition;

import oldmana.md.server.card.control.condition.logic.ConditionAnd;
import oldmana.md.server.state.ActionStatePlay;

public class ConditionBankBasic extends ConditionAnd
{
	public ConditionBankBasic()
	{
		super(new ConditionOwnTurn(), new ConditionState(ActionStatePlay.class));
	}
}
