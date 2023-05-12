package oldmana.md.server.ai.normal.wincondition;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.rules.win.WinCondition;

import java.util.List;

public interface WinConditionChecker<WC extends WinCondition>
{
	double getProgress(WC condition, Player player);
	
	double getProgressWith(WC condition, Player player, List<Card> with);
	
	double getProgressWithout(WC condition, Player player, List<Card> without);
}
