package oldmana.md.server.card.control.condition;

import oldmana.md.server.card.control.condition.logic.ConditionAnd;
import oldmana.md.server.state.ActionStatePlay;

public class ConditionPlayBasic extends ConditionAnd
{
	public ConditionPlayBasic()
	{
		super(new ConditionOwnTurn(), new ConditionState(ActionStatePlay.class), new ConditionCanPlayCard());
	}
}
