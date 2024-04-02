package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A struct with named structs under it.
 */
public class RuleStructObject extends RuleStructNamed
{
	private Map<String, RuleStruct> children = new HashMap<String, RuleStruct>();
	
	@Override
	public JSONObject toJSONSchema()
	{
		JSONObject obj = super.toJSONSchema();
		obj.put("type", "object");
		JSONObject children = new JSONObject();
		this.children.forEach((name, child) -> children.put(name, child.toJSONSchema()));
		obj.put("children", children);
		return obj;
	}
	
	@Override
	public void loadSchema(JSONObject obj)
	{
		super.loadSchema(obj);
		JSONObject children = obj.getJSONObject("children");
		for (String childName : children.keySet())
		{
			JSONObject childObj = children.getJSONObject(childName);
			RuleStructNamed childSchema = (RuleStructNamed) RuleStruct.createSchemaObject(childObj);
			childSchema.setJsonName(childName);
			childSchema.loadSchema(childObj);
			childSchema.setParent(this);
		}
	}
	
	@Override
	public GameRule generateDefaults()
	{
		Map<String, GameRule> rules = new HashMap<String, GameRule>();
		children.forEach((key, rule) -> rules.put(key, rule.generateDefaults()));
		return new GameRule(this, rules, true);
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		JSONObject json = (JSONObject) obj;
		Map<String, GameRule> map = new HashMap<String, GameRule>();
		children.forEach((key, child) ->
				map.put(key, json.has(key) ? new GameRule(child, json.get(key)) : child.generateDefaults()));
		return map;
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		Map<String, GameRule> map = rule.getValueAsMap();
		JSONObject json = new JSONObject();
		map.forEach((key, subrule) ->
		{
			Object subruleJson = subrule.toJSON();
			if (subruleJson != null) // If it's null, it must've been reduced
			{
				json.put(key, subruleJson);
			}
		});
		if (isReducible() && json.isEmpty())
		{
			return null;
		}
		return json;
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return children.size() + " Item" + (children.size() != 1 ? "s" : "");
	}
	
	@Override
	public RuleStruct getChild(String name)
	{
		return children.get(name);
	}
	
	@Override
	public void addChild(RuleStruct child)
	{
		if (!(child instanceof RuleStructNamed))
		{
			throw new IllegalArgumentException("Objects cannot have unnamed RuleStructs");
		}
		children.put(((RuleStructNamed) child).getJsonName(), child);
	}
	
	public static class RuleObjectBuilder extends RuleNamedBuilder<RuleStructObject, RuleObjectBuilder>
	{
		public static RuleObjectBuilder from(RuleStruct parent)
		{
			return new RuleObjectBuilder(parent);
		}
		
		public RuleObjectBuilder(RuleStruct parent)
		{
			super(new RuleStructObject(), parent);
		}
	}
}
