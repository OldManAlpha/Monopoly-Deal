package oldmana.md.server.rules.win;

import oldmana.md.server.Player;

public class MoneyCondition extends WinCondition
{
	private int threshold;
	
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
