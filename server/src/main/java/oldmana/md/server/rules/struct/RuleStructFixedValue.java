package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import oldmana.md.server.rules.ValueType;
import org.json.JSONObject;

import java.util.Arrays;

public class RuleStructFixedValue<T> extends RuleStruct implements JsonValue<T>
{
	private ValueType<T> valueType;
	private T value;
	
	protected RuleStructFixedValue()
	{
		setName("Value");
	}
	
	protected RuleStructFixedValue(T value)
	{
		valueType = (ValueType<T>) ValueType.getByClass(value.getClass());
		this.value = value;
		setName("Value");
	}
	
	@Override
	public JSONObject toJSONSchema()
	{
		JSONObject obj = super.toJSONSchema();
		obj.put("type", "fixedValue");
		obj.put("value", value);
		return obj;
	}
	
	@Override
	public void loadSchema(JSONObject obj)
	{
		super.loadSchema(obj);
		value = (T) obj.get("value");
		valueType = (ValueType<T>) ValueType.getByClass(value.getClass());
	}
	
	@Override
	public GameRule generateDefaults()
	{
		return new GameRule(this, value);
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		return value;
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		return value;
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return value.toString();
	}
	
	@Override
	public ValueType<T> getValueType()
	{
		return valueType;
	}
	
	@Override
	public T getDefaultValue()
	{
		return value;
	}
	
	public static <T> RuleStructFixedValue<T> of(T defaultValue)
	{
		return of(defaultValue, defaultValue.toString());
	}
	
	public static <T> RuleStructFixedValue<T> of(T defaultValue, String name)
	{
		return of(defaultValue, name, "<No Description>");
	}
	
	public static <T> RuleStructFixedValue<T> of(T defaultValue, String name, String... description)
	{
		RuleStructFixedValue<T> rule = new RuleStructFixedValue<T>(defaultValue);
		rule.setName(name);
		rule.setDescription(Arrays.asList(description));
		return rule;
	}
}
