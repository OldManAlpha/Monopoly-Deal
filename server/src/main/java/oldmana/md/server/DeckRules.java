package oldmana.md.server;

import java.util.HashMap;
import java.util.Map;

public class DeckRules implements Cloneable
{
	private Map<String, GameRule> rules;
	
	public DeckRules()
	{
		
	}
	
	public DeckRules(Map<String, GameRule> rules)
	{
		this.rules = rules;
	}
	
	public GameRule getRule(String name)
	{
		return rules.get(name);
	}
	
	@Override
	public DeckRules clone()
	{
		Map<String, GameRule> copy = new HashMap<String, GameRule>();
		rules.forEach((name, rule) -> copy.put(name, rule));
		return new DeckRules(copy);
	}
	
//	Win Condition:
//	{
//		Type: <Monopoly/Property/Money/Unique Colors>
//		Amount: 3
//	}
//	Property Mechanics:
//	{
//		Type: <One Set Per Color/Freely Movable Wilds>
//		
//	}
	
	// Rules:
	// Win Condition
	//   Number of Monopolies (default)
	//     Amount (3)
	//   Number of Properties
	//     Amount (?)
	//   Amount of Money
	//     Amount (?)
	//   Number of Unique Colors
	//     Amount (10)
	// Turn Count (3)
	// Draw 5 Immediately After Running Out Of Cards (false)
	// Deal Breakers Discard Sets (false)
	// 2-Color Rent Charges All (true)
	// Multi-Color Rent Charges All (false)
	// Allow non-action cards to be undone (true)
	// Moving Property Costs Turn (false)
	// Automatically Calculate Highest Rent (true)
	// Property Mechanics
	//   One Set Per Color
	//     Except When Over Maximum Property Count (false)
	//   Freely Movable Wilds (default)
	// Building Mechanics
	//   
}
