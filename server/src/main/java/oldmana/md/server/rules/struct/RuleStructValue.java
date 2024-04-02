package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import oldmana.md.server.rules.ValueType;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A primitive value.
 * @param <T>
 */
public class RuleStructValue<T> extends RuleStruct implements JsonValue<T>, JsonParsable
{
	private ValueType<T> valueType;
	private T defaultValue;
	
	public RuleStructValue() {}
	
	private RuleStructValue(T defaultValue)
	{
		valueType = (ValueType<T>) ValueType.getByClass(defaultValue.getClass());
		this.defaultValue = defaultValue;
		setName("Value");
		setDescription(new ArrayList<String>());
	}
	
	/**
	 * Right now, this is only called by arrays
	 */
	@Override
	public JSONObject toJSONSchema()
	{
		JSONObject obj = super.toJSONSchema();
		obj.put("type", "arrayValue");
		obj.put("valueType", valueType.getJsonName());
		obj.put("defaultValue", defaultValue);
		return obj;
	}
	
	@Override
	public void loadSchema(JSONObject obj)
	{
		super.loadSchema(obj);
		defaultValue = (T) obj.get("defaultValue");
		valueType = (ValueType<T>) ValueType.getByClass(defaultValue.getClass());
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
		return (isReducible() || getParent().isReducible()) && rule.getValue().equals(defaultValue) ? null : rule.getValue();
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
