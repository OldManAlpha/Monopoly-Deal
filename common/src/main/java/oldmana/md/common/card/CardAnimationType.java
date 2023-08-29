package oldmana.md.common.card;

import java.util.HashMap;
import java.util.Map;

public enum CardAnimationType
{
	NORMAL("Normal", 1),
	IMPORTANT("Important", 3);
	
	private static final Map<String, CardAnimationType> jsonMap = new HashMap<String, CardAnimationType>();
	static
	{
		for (CardAnimationType type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	private final double defaultTime;
	
	CardAnimationType(String jsonName, double defaultTime)
	{
		this.jsonName = jsonName;
		this.defaultTime = defaultTime;
	}
	
	public int getID()
	{
		return ordinal();
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static CardAnimationType fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
	
	public double getDefaultTime()
	{
		return defaultTime;
	}
	
	public static CardAnimationType fromID(int id)
	{
		return values()[id];
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
