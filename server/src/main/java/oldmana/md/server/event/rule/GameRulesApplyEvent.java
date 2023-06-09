package oldmana.md.server.event.rule;

import oldmana.md.server.event.Event;
import oldmana.md.server.mod.ServerMod;
import oldmana.md.server.rules.GameRule;

public class GameRulesApplyEvent extends Event
{
	private GameRule rules;
	
	public GameRulesApplyEvent(GameRule rules)
	{
		this.rules = rules;
	}
	
	public GameRule getRootRule()
	{
		return rules;
	}
	
	public GameRule getRulesFor(ServerMod mod)
	{
		return getRulesFor(mod.getName());
	}
	
	public GameRule getRulesFor(String modName)
	{
		return rules.getSubrule("modRules").getSubrule(modName);
	}
}
