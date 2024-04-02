package oldmana.md.server.rules;

public enum DrawExtraCardsPolicy implements JsonEnum
{
	IMMEDIATELY("Immediately"),
	IMMEDIATELY_AFTER_ACTION("ImmediatelyAfterAction"),
	NEXT_DRAW("NextDraw"),
	NEVER("Never");
	
	private static final JsonEnumMapper<DrawExtraCardsPolicy> map = new JsonEnumMapper<DrawExtraCardsPolicy>(DrawExtraCardsPolicy.class);
	
	private final String jsonName;
	
	DrawExtraCardsPolicy(String jsonName)
	{
		this.jsonName = jsonName;
	}
	
	@Override
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static DrawExtraCardsPolicy fromJson(String jsonName)
	{
		return map.fromJson(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
