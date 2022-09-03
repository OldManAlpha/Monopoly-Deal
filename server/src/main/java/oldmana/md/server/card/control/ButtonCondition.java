package oldmana.md.server.card.control;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

@FunctionalInterface
public interface ButtonCondition
{
	boolean evaluate(Player player, Card card);
}
