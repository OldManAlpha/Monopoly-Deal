package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * A key with fixed key, value, or object choices
 */
public class RuleStructOption extends RuleStructNamed implements JsonParsable
{
	private Map<String, RuleStruct> choices = new HashMap<String, RuleStruct>();
	private String defaultChoiceName;
	
	@Override
	public GameRule generateDefaults()
	{
		return new GameRule(this, getDefaultChoice().generateDefaults(), true);
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		if (obj instanceof JSONObject)
		{
			JSONObject json = (JSONObject) obj;
			String key = json.keys().next();
			Object val = json.get(key);
			RuleStruct choice = getChild(key);
			return new GameRule(choice, val);
		}
		if (getChild(obj.toString()) != null)
		{
			return new GameRule(getChild(obj.toString()), obj);
		}
		return generateDefaults();
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		GameRule choice = rule.getChoice();
		RuleStruct choiceStruct = choice.getRuleStruct();
		if (isReducible() && choiceStruct == getDefaultChoice())
		{
			return null;
		}
		if (choiceStruct instanceof RuleStructNamed) // Either Key or Object
		{
			JSONObject obj = new JSONObject();
			obj.put(choice.getJsonName(), choice.toJSON());
			return obj;
		}
		return choice.toJSON();
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return rule.getChoiceStruct().getName();
	}
	
	@Override
	public RuleStruct getChild(String name)
	{
		return choices.get(name);
	}
	
	@Override
	public void addChild(RuleStruct child)
	{
		if (child instanceof RuleStructNamed)
		{
			choices.put(((RuleStructNamed) child).getJsonName(), child);
		}
		else if (child instanceof JsonValue)
		{
			choices.put(((JsonValue<?>) child).getDefaultValue().toString(), child);
		}
	}
	
	public Map<String, RuleStruct> getChoices()
	{
		return choices;
	}
	
	public List<String> getChoiceNames()
	{
		return choices.values().stream().map(RuleStruct::getName).collect(Collectors.toList());
	}
	
	public RuleStruct getDefaultChoice()
	{
		return choices.get(defaultChoiceName);
	}
	
	public String findKey(String name)
	{
		for (Entry<String, RuleStruct> entry : choices.entrySet())
		{
			if (entry.getValue().getName().equalsIgnoreCase(name))
			{
				return entry.getKey();
			}
		}
		return null;
	}
	
	@Override
	public Object parse(String input)
	{
		RuleStruct rule = choices.get(input);
		if (rule == null)
		{
			throw new IllegalArgumentException("'" + input + "' is not an option");
		}
		return rule.generateDefaults();
	}
	
	public static class RuleOptionBuilder extends RuleNamedBuilder<RuleStructOption, RuleOptionBuilder>
	{
		public static RuleOptionBuilder from(RuleStruct parent)
		{
			return new RuleOptionBuilder(parent);
		}
		
		public RuleOptionBuilder(RuleStruct parent)
		{
			super(new RuleStructOption(), parent);
		}
		
		public RuleOptionBuilder addChoice(Object choice)
		{
			getRule().addChild(RuleStructFixedValue.of(choice));
			return this;
		}
		
		public RuleOptionBuilder addChoice(Object choice, String name)
		{
			getRule().addChild(RuleStructFixedValue.of(choice, name));
			return this;
		}
		
		public RuleOptionBuilder addChoice(Object choice, String name, String... description)
		{
			getRule().addChild(RuleStructFixedValue.of(choice, name, description));
			return this;
		}
		
		public RuleOptionBuilder addChoices(Object... choices)
		{
			for (Object choice : choices)
			{
				getRule().addChild(RuleStructFixedValue.of(choice));
			}
			return this;
		}
		
		public RuleOptionBuilder addChoices(Object[]... choices)
		{
			return this;
		}
		
		public RuleOptionBuilder defaultChoice(Object choice)
		{
			getRule().defaultChoiceName = choice.toString();
			return this;
		}
	}
}
