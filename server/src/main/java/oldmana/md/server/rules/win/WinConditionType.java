package oldmana.md.server.rules.win;

import oldmana.md.server.rules.GameRule;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum WinConditionType
{
	PROPERTY_SET("propertySets", PropertySetCondition::new),
	MONEY("money", MoneyCondition::new),
	COLOR("color", ColorCondition::new);
	
	private static final Map<String, WinConditionType> jsonMap = new HashMap<String, WinConditionType>();
	static
	{
		for (WinConditionType type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	private final Function<GameRule, WinCondition> factory;
	
	WinConditionType(String jsonName, Function<GameRule, WinCondition> factory)
	{
		this.jsonName = jsonName;
		this.factory = factory;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public WinCondition create(GameRule rule)
	{
		return factory.apply(rule);
	}
	
	public static WinConditionType fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
	
	public static WinConditionType fromRule(GameRule rule)
	{
		return jsonMap.get(rule.getJsonName());
	}
}
