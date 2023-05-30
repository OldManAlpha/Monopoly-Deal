package oldmana.md.server.rules;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameRule
{
	private RuleStruct rule;
	private Object value;
	
	public GameRule(GameRule toCopy)
	{
		this.rule = toCopy.rule;
		Object copyValue = toCopy.getValue();
		if (toCopy.isPrimitive())
		{
			value = copyValue;
		}
		else if (copyValue instanceof GameRule)
		{
			value = new GameRule((GameRule) copyValue);
		}
		else if (copyValue instanceof Map)
		{
			Map<String, GameRule> map = new HashMap<String, GameRule>();
			((Map<String, GameRule>) copyValue).forEach((key, rule) -> map.put(key, new GameRule(rule)));
			value = map;
		}
	}
	
	public GameRule(RuleStruct rule, Object value)
	{
		this.rule = rule;
		this.value = rule.fromJSON(value);
	}
	
	public GameRule(RuleStruct rule, Object value, boolean def)
	{
		this.rule = rule;
		this.value = value;
	}
	
	public RuleStruct getRuleStruct()
	{
		return rule;
	}
	
	public RuleStruct getChoiceStruct()
	{
		return ((GameRule) value).getRuleStruct();
	}
	
	public Object getValue()
	{
		return value;
	}
	
	/**
	 * If the value is a String and should be parsed as another type, it will automatically be done.
	 * @param value The new value of this rule
	 */
	public void setValue(Object value)
	{
		// TODO: Move function to RuleStructs
		if (rule instanceof RuleStructValue)
		{
			RuleStructValue<?> valueStruct = (RuleStructValue<?>) rule;
			if (value instanceof String)
			{
				this.value = valueStruct.parse((String) value);
				return;
			}
			if (!valueStruct.getValueType().isCompatible(value))
			{
				throw new IllegalArgumentException("Invalid value type for rule");
			}
			this.value = value;
		}
		else if (rule instanceof RuleStructKey)
		{
			getValueAsRule().setValue(value);
		}
		else if (rule instanceof RuleStructOption)
		{
			this.value = ((RuleStructOption) rule).parse((String) value);
		}
	}
	
	public <T> T getValueAs(Class<T> type)
	{
		return (T) value;
	}
	
	public Object getDeepValue()
	{
		return value instanceof GameRule ? ((GameRule) value).getValue() : value;
	}
	
	public int getInteger()
	{
		return (int) getDeepValue();
	}
	
	public double getDouble()
	{
		return (double) getDeepValue();
	}
	
	public String getString()
	{
		return (String) getDeepValue();
	}
	
	public boolean getBoolean()
	{
		return (boolean) getDeepValue();
	}
	
	public String getDisplayValue()
	{
		return rule.getDisplayValue(this);
	}
	
	/**
	 * Only applicable to rules that are objects.
	 * @param name The name of the subrule
	 * @return The subrule
	 */
	public GameRule getSubrule(String name)
	{
		if (rule instanceof RuleStructOption && ((RuleStructNamed) getChoice().getRuleStruct()).getJsonName().equalsIgnoreCase(name))
		{
			return getChoice();
		}
		return getValueAsMap().get(name);
	}
	
	public GameRule traverse(String path)
	{
		if (path == null || path.equals(""))
		{
			return this;
		}
		GameRule rule = this;
		for (String s : path.split("\\."))
		{
			rule = rule.getSubrule(s);
			if (rule.getRuleStruct() instanceof RuleStructOption && !(value instanceof Map))
			{
				rule = rule.getChoice();
			}
		}
		return rule;
	}
	
	/**
	 * Only applicable to rules that are options.
	 * @return The choice of this rule
	 */
	public GameRule getChoice()
	{
		return value instanceof GameRule ? (GameRule) value : ((Map<String, GameRule>) value).values().iterator().next();
	}
	
	/**
	 * Only applicable to rules that are options.
	 * @param choice The new choice
	 */
	public void setChoice(String choice)
	{
		value = ((RuleStructOption) rule).parse(choice);
	}
	
	/**
	 * Only applicable to rules that have json names.
	 * @return The json name of this rule
	 */
	public String getJsonName()
	{
		return ((RuleStructNamed) rule).getJsonName();
	}
	
	/**
	 * Only applicable to rules that are objects.
	 * @return The object map of this rule
	 */
	public Map<String, GameRule> getValueAsMap()
	{
		return (Map<String, GameRule>) value;
	}
	
	public Map<String, GameRule> getView()
	{
		if (value instanceof Map)
		{
			return ((Map<String, GameRule>) value).entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.sorted((e1, e2) ->
			{
				RuleStruct e1Struct = e1.getValue().getRuleStruct();
				RuleStruct e2Struct = e2.getValue().getRuleStruct();
				return (e1Struct instanceof RuleStructObject || e1Struct instanceof RuleStructOption) &&
						!(e2Struct instanceof RuleStructObject || e2Struct instanceof RuleStructOption) ? -1 : 0;
			}).collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
		}
		Map<String, GameRule> map = new HashMap<String, GameRule>();
		map.put(getJsonName(), getValueAsRule());
		return map;
	}
	
	/**
	 * Only applicable to rules that are not objects or primitives.
	 * @return The GameRule under this one
	 */
	public GameRule getValueAsRule()
	{
		return (GameRule) value;
	}
	
	/**
	 * @return True if the rule holds a primitive value
	 */
	public boolean isPrimitive()
	{
		return !(value instanceof Map || value instanceof GameRule);
	}
	
	public JSONObject toJSONObject()
	{
		return (JSONObject) toJSON();
	}
	
	public Object toJSON()
	{
		return rule.toJSON(this);
	}
}
