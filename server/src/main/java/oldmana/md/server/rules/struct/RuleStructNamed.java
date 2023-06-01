package oldmana.md.server.rules.struct;

public abstract class RuleStructNamed extends RuleStruct
{
	private String jsonName;
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	protected void setJsonName(String jsonName)
	{
		this.jsonName = jsonName;
	}
	
	public static class RuleNamedBuilder<RS extends RuleStructNamed, B extends RuleNamedBuilder> extends RuleBuilder<RS, B>
	{
		protected RuleNamedBuilder(RS rule, RuleStruct parent)
		{
			super(rule, parent);
		}
		
		public B jsonName(String jsonName)
		{
			getRule().setJsonName(jsonName);
			return getThis();
		}
	}
}
