package oldmana.md.server.card;

import java.util.HashMap;
import java.util.Map;

/**
 * When to perform a specific process in the card playing process.
 */
public enum CardPlayStage
{
	/** Perform process before calling play logic **/
	BEFORE_PLAY("BeforePlay"),
	/** Perform process right before calling play logic **/
	RIGHT_BEFORE_PLAY("RightBeforePlay"),
	/** Perform process right after calling play logic **/
	RIGHT_AFTER_PLAY("RightAfterPlay"),
	/** Perform process after calling play logic **/
	AFTER_PLAY("AfterPlay"),
	/** Do not automatically perform process **/
	MANUAL("Manual");
	
	private static final Map<String, CardPlayStage> jsonMap = new HashMap<String, CardPlayStage>();
	static
	{
		for (CardPlayStage type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	
	CardPlayStage(String jsonName)
	{
		this.jsonName = jsonName;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static CardPlayStage fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
