package oldmana.md.common.card;

import java.util.HashMap;
import java.util.Map;

public enum CardAnimationType
{
	NORMAL("Normal"),
	IMPORTANT("Important");
	
	private static final Map<String, CardAnimationType> jsonMap = new HashMap<String, CardAnimationType>();
	static
	{
		for (CardAnimationType type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	
	CardAnimationType(String jsonName)
	{
		this.jsonName = jsonName;
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
