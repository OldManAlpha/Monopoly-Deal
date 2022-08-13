package oldmana.md.server;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class GameRule implements Cloneable
{
	private String name;
	private String friendlyName;
	private String description;
	
	private Object value;
	
	public GameRule(String name, String friendlyName, String description)
	{
		this.name = name;
		this.friendlyName = friendlyName;
		this.description = description;
		value = new HashMap<String, GameRule>();
	}
	
	public GameRule(String name, String friendlyName, String description, Object defaultValue)
	{
		this(name, friendlyName, description);
		value = defaultValue;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getFriendlyName()
	{
		return friendlyName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setValue(Map<String, GameRule> value)
	{
		this.value = value;
	}
	
	public void addRule(GameRule rule)
	{
		getRules().put(rule.getName(), rule);
	}
	
	public void setValue(boolean value)
	{
		this.value = value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public GameRule getRule(String key)
	{
		return getRules().get(key);
	}
	
	public Map<String, GameRule> getRules()
	{
		return (Map<String, GameRule>) value;
	}
	
	public boolean getBoolean()
	{
		return (boolean) value;
	}
	
	public int getInteger()
	{
		return (int) value;
	}
	
	public double getDouble()
	{
		return (double) value;
	}
	
	public String getString()
	{
		return (String) value;
	}
	
	public boolean hasChildren()
	{
		return value instanceof Map;
	}
	
	public Object toJSON()
	{
		if (hasChildren())
		{
			JSONObject json = new JSONObject();
			for (GameRule rule : getRules().values())
			{
				json.put(rule.getName(), rule.toJSON());
			}
			return json;
		}
		return value;
	}
	
	@Override
	public GameRule clone()
	{
		if (hasChildren())
		{
			Map<String, GameRule> rules = getRules();
			Map<String, GameRule> copy = new HashMap<String, GameRule>();
			rules.forEach((name, rule) -> copy.put(name, rule.clone()));
			return new GameRule(name, friendlyName, description, copy);
		}
		return new GameRule(name, friendlyName, description, value);
	}
}
