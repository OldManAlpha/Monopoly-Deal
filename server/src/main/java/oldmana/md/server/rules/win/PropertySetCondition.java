package oldmana.md.server.rules.win;

import oldmana.md.server.Player;

public class PropertySetCondition extends WinCondition
{
	private int sets;
	private boolean unique = true;
	
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
