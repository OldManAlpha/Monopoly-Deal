package oldmana.md.server.rules;

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
		json.forEach((key, val) -> map.put(key, new GameRule(getChild(key), val)));
		return map;
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		Map<String, GameRule> map = rule.getValueAsMap();
		JSONObject json = new JSONObject();
		map.forEach((key, subrule) -> json.put(key, subrule.toJSON()));
		return json;
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return children.size() + " Items";
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
