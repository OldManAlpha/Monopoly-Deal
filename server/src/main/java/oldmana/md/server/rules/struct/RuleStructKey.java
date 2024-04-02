package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import org.json.JSONObject;

/**
 * A key which has a primitive value.
 */
public class RuleStructKey extends RuleStructNamed implements JsonParsable
{
	private RuleStructValue<?> child;
	
	@Override
	public JSONObject toJSONSchema()
	{
		JSONObject obj = super.toJSONSchema();
		obj.put("type", "value");
		obj.put("valueType", child.getValueType().getJsonName());
		obj.put("defaultValue", child.getDefaultValue());
		return obj;
	}
	
	@Override
	public void loadSchema(JSONObject obj)
	{
		super.loadSchema(obj);
		addChild(RuleStructValue.of(this, obj.get("defaultValue")));
	}
	
	public RuleStructValue<?> getChild()
	{
		return child;
	}
	
	@Override
	public void addChild(RuleStruct child)
	{
		this.child = (RuleStructValue<?>) child;
	}
	
	@Override
	public GameRule generateDefaults()
	{
		return new GameRule(this, child.generateDefaults(), true);
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		return new GameRule(child, child.fromJSON(obj));
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		return getChild().toJSON(rule.getValueAsRule());
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return getName();
	}
	
	@Override
	public Object parse(String input)
	{
		return child.parse(input);
	}
	
	public static class RuleKeyBuilder extends RuleNamedBuilder<RuleStructKey, RuleKeyBuilder>
	{
		public static RuleKeyBuilder from(RuleStruct parent)
		{
			return new RuleKeyBuilder(parent);
		}
		
		public RuleKeyBuilder(RuleStruct parent)
		{
			super(new RuleStructKey(), parent);
		}
		
		public RuleKeyBuilder defaultValue(Object value)
		{
			getRule().addChild(RuleStructValue.of(getRule(), value));
			return this;
		}
	}
}
