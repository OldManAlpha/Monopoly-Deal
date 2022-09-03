package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.state.ActionState;

public class ConditionState extends AbstractButtonCondition
{
	private Class<? extends ActionState> state;
	
	public ConditionState(Class<? extends ActionState> state)
	{
		this.state = state;
	}
	
	@Override
	public boolean evaluate(Player player, Card card)
	{
		return state.isAssignableFrom(getServer().getGameState().getActionState().getClass());
	}
}
