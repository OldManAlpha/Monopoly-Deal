package oldmana.md.server.card.control.condition.logic;

import oldmana.md.server.card.control.condition.AbstractButtonCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ConditionLogical extends AbstractButtonCondition
{
	private List<AbstractButtonCondition> conditions = new ArrayList<AbstractButtonCondition>();
	
	public ConditionLogical(AbstractButtonCondition... conditions)
	{
		this.conditions.addAll(Arrays.asList(conditions));
	}
	
	public ConditionLogical(List<AbstractButtonCondition> conditions)
	{
		this.conditions.addAll(conditions);
	}
	
	public List<AbstractButtonCondition> getConditions()
	{
		return conditions;
	}
	
	public void addCondition(AbstractButtonCondition condition)
	{
		conditions.add(condition);
	}
	
	public void removeCondition(AbstractButtonCondition condition)
	{
		conditions.remove(condition);
	}
}
