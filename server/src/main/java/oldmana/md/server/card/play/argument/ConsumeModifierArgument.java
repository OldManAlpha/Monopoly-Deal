package oldmana.md.server.card.play.argument;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.ModifierCard;
import oldmana.md.server.card.play.PlayArgument;
import oldmana.md.server.card.play.PlayArguments;

/**
 * Indicates that the {@link ModifierCard} should be consumed. Typically, this argument should only be used by modifier
 * targets when they want to consume modifiers. This argument will cause ModifierCards to actually consume moves and
 * move to the discard pile. ModifierCards likely should check for this argument in
 * {@link Card#doPlay(Player, PlayArguments)} so that they don't perform illegal logic.
 */
public class ConsumeModifierArgument implements PlayArgument
{
	private Card target;
	
	public ConsumeModifierArgument(Card target)
	{
		this.target = target;
	}
	
	public Card getTarget()
	{
		return target;
	}
}
