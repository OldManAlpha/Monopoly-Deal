package oldmana.md.server.rules.win;

import oldmana.md.server.Player;
import oldmana.md.server.rules.GameRule;

public class MoneyCondition extends WinCondition
{
	private int threshold;
	
	public MoneyCondition(GameRule rule)
	{
		threshold = rule.getInteger();
	}
	
	public MoneyCondition(int threshold)
	{
		this.threshold = threshold;
	}
	
	@Override
	public boolean isWinner(Player player)
	{
		return player.getBank().getTotalValue() >= threshold;
	}
}
