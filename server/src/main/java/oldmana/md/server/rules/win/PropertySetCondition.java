package oldmana.md.server.rules.win;

import oldmana.md.server.Player;
import oldmana.md.server.rules.GameRule;

public class PropertySetCondition extends WinCondition
{
	private int sets;
	private boolean unique = true;
	
	public PropertySetCondition(GameRule rule)
	{
		sets = rule.getSubrule("setsRequired").getInteger();
		unique = rule.getSubrule("unique").getBoolean();
	}
	
	public PropertySetCondition(int sets)
	{
		this.sets = sets;
	}
	
	public PropertySetCondition(int sets, boolean unique)
	{
		this.sets = sets;
		this.unique = unique;
	}
	
	public int getSetCount()
	{
		return sets;
	}
	
	public boolean needsUnique()
	{
		return unique;
	}
	
	@Override
	public boolean isWinner(Player player)
	{
		return unique ? player.getUniqueMonopolyCount() >= sets : player.getMonopolyCount() >= sets;
	}
}
