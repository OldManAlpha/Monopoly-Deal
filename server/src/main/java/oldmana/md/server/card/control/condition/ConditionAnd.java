package oldmana.md.server.card.control.condition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConditionAnd extends ButtonCondition
{
	private List<ButtonCondition> conditions = new ArrayList<ButtonCondition>();
	
	public ConditionAnd(ButtonCondition... conditions)
	{
		this.conditions.addAll(Arrays.asList(conditions));
	}
	
	public ConditionAnd(List<ButtonCondition> conditions)
	{
		this.conditions.addAll(conditions);
	}
	
	public void addCondition(ButtonCondition condition)
	{
		conditions.add(condition);
	}
	
	public List<ButtonCondition> getConditions()
	{
		return conditions;
	}
	
	@Override
	public boolean isTrue(Player player, Card card)
	{
		for (ButtonCondition condition : conditions)
		{
			if (!condition.isTrue(player, card))
			{
				return false;
			}
		}
		return true;
	}
}
