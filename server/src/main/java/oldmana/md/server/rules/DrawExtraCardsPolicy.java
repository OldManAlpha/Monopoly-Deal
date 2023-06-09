package oldmana.md.server.rules;

import java.util.HashMap;
import java.util.Map;

public enum DrawExtraCardsPolicy
{
	IMMEDIATELY("Immediately"),
	IMMEDIATELY_AFTER_ACTION("ImmediatelyAfterAction"),
	NEXT_DRAW("NextDraw"),
	NEVER("Never");
	
	private static final Map<String, DrawExtraCardsPolicy> jsonMap = new HashMap<String, DrawExtraCardsPolicy>();
	static
	{
		for (DrawExtraCardsPolicy type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	
	DrawExtraCardsPolicy(String jsonName)
	{
		this.jsonName = jsonName;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static DrawExtraCardsPolicy fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
