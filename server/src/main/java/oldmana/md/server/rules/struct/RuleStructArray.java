package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Arrays contain a list of SchemaValues or SchemaObjects.
 */
public class RuleStructArray extends RuleStructNamed
{
	private RuleStruct arrayType;
	private int minElements = 2;
	
	@Override
	public JSONObject toJSONSchema()
	{
		JSONObject obj = super.toJSONSchema();
		obj.put("type", "array");
		obj.put("arrayValue", arrayType.toJSONSchema());
		return obj;
	}
	
	@Override
	public void loadSchema(JSONObject obj)
	{
		super.loadSchema(obj);
		JSONObject valObj = obj.getJSONObject("arrayValue");
		RuleStruct valSchema = RuleStruct.createSchemaObject(valObj);
		valSchema.setParent(this);
		valSchema.loadSchema(valObj);
	}
	
	@Override
	public GameRule generateDefaults()
	{
		JSONArray array = new JSONArray();
		for (int i = 0 ; i < minElements ; i++)
		{
			array.put(generateDefaultElement());
		}
		return new GameRule(this, array, true);
	}
	
	public GameRule generateDefaultElement()
	{
		return arrayType.generateDefaults();
	}
	
	@Override
	public Object fromJSON(Object obj)
	{
		JSONArray json = (JSONArray) obj;
		List<Object> list = new ArrayList<Object>();
		for (Object element : json)
		{
			list.add(arrayType.fromJSON(element));
		}
		return list;
	}
	
	@Override
	public Object toJSON(GameRule rule)
	{
		List<GameRule> list = rule.getValueAs(List.class);
		JSONArray json = new JSONArray();
		for (GameRule element : list)
		{
			json.put(arrayType.toJSON(element));
		}
		return json;
	}
	
	@Override
	public String getDisplayValue(GameRule rule)
	{
		return "Array";
	}
	
	@Override
	public RuleStruct getChild(String name)
	{
		return arrayType;
	}
	
	@Override
	public void addChild(RuleStruct child)
	{
		arrayType = child;
	}
	
	public static class RuleArrayBuilder extends RuleNamedBuilder<RuleStructArray, RuleArrayBuilder>
	{
		public static RuleArrayBuilder from(RuleStruct parent)
		{
			return new RuleArrayBuilder(parent);
		}
		
		public RuleArrayBuilder(RuleStruct parent)
		{
			super(new RuleStructArray(), parent);
		}
	}
}
