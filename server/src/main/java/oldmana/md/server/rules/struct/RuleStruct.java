package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.GameRule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class RuleStruct
{
	private RuleStruct parent;
	private String name;
	private List<String> description;
	private boolean reducible;
	
	public boolean hasParent()
	{
		return parent != null;
	}
	
	public RuleStruct getParent()
	{
		return parent;
	}
	
	public RuleStruct getObjectParent()
	{
		return parent instanceof RuleStructObject ? parent : parent.getParent();
	}
	
	protected void setParent(RuleStruct parent)
	{
		this.parent = parent;
		parent.addChild(this);
	}
	
	protected void addChild(RuleStruct child)
	{
		throw new UnsupportedOperationException("There are no children in this structure");
	}
	
	public RuleStruct getChild(String name)
	{
		throw new UnsupportedOperationException("There are no children in this structure");
	}
	
	public String getPath()
	{
		String path = null;
		RuleStruct rs = this;
		while (rs != null)
		{
			if (rs instanceof RuleStructNamed && rs.getParent() != null)
			{
				path = ((RuleStructNamed) rs).getJsonName() + (path != null ? "." + path : "");
			}
			rs = rs.getParent();
		}
		return path == null ? "" : path;
	}
	
	public List<String> getDisplayPathList()
	{
		List<String> path = new ArrayList<String>();
		RuleStruct rs = this;
		while (rs != null)
		{
			if (rs instanceof RuleStructNamed && rs.getParent() != null)
			{
				path.add(0, rs.getName());
			}
			rs = rs.getParent();
		}
		return path;
	}
	
	public String getName()
	{
		return name;
	}
	
	protected void setName(String name)
	{
		this.name = name;
	}
	
	public List<String> getDescription()
	{
		return description;
	}
	
	public void setDescription(List<String> description)
	{
		this.description = description;
	}
	
	public boolean isReducible()
	{
		return reducible;
	}
	
	protected void setReducible(boolean reducible)
	{
		this.reducible = reducible;
	}
	
	/**
	 * Generates a default GameRule stemming from this RuleStruct.
	 * @return A newly generated default GameRule
	 */
	public abstract GameRule generateDefaults();
	
	/**
	 * Creates a GameRule or object map from the provided JSON value.
	 * @param obj The JSON value
	 * @return An appropriate object from the JSON value
	 */
	public abstract Object fromJSON(Object obj);
	
	/**
	 * Returns the JSON representation of a GameRule.
	 * @param rule The GameRule to turn into JSON
	 * @return JSON that represents the rule
	 */
	public abstract Object toJSON(GameRule rule);
	
	public abstract String getDisplayValue(GameRule rule);
	
	public static class RuleBuilder<RS extends RuleStruct, B extends RuleBuilder>
	{
		private RS rule;
		private RuleStruct parent;
		
		protected RuleBuilder(RS rule, RuleStruct parent)
		{
			this.rule = rule;
			this.parent = parent;
		}
		
		public B name(String name)
		{
			rule.setName(name);
			return getThis();
		}
		
		public B description(String... description)
		{
			rule.setDescription(Stream.of(description).collect(Collectors.toList()));
			return getThis();
		}
		
		public B reducible(boolean reducible)
		{
			rule.setReducible(reducible);
			return getThis();
		}
		
		public RS register()
		{
			if (parent != null)
			{
				getRule().setParent(parent);
			}
			return rule;
		}
		
		protected B getThis()
		{
			return (B) this;
		}
		
		protected RS getRule()
		{
			return rule;
		}
	}
}
