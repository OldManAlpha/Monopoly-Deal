package oldmana.md.server.rules;

/**
 * A primitive value.
 * @param <T>
 */
public class RuleStructValue<T> extends RuleStruct implements JsonValue<T>, JsonParsable
{
	private ValueType<T> valueType;
	private T defaultValue;
	
	private RuleStructValue(T defaultValue)
	{
		valueType = (ValueType<T>) ValueType.getByClass(defaultValue.getClass());
		this.defaultValue = defaultValue;
		setName("Value");
	}
	
	@Override
	public GameRule generateDefaults()
	{
		return new GameRule(this, defaultValue, true);
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		return obj;
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		return rule.getValue();
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return valueType.toDisplay((T) rule.getValue());
	}
	
	@Override
	public ValueType<T> getValueType()
	{
		return valueType;
	}
	
	public T getDefaultValue()
	{
		return defaultValue;
	}
	
	@Override
	public T parse(String input)
	{
		return valueType.parse(input);
	}
	
	public static <T> RuleStructValue<T> of(RuleStruct parent, T defaultValue)
	{
		RuleStructValue<T> rule = new RuleStructValue<T>(defaultValue);
		rule.setParent(parent);
		return rule;
	}
}