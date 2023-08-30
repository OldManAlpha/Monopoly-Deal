package oldmana.md.server.card;

import oldmana.md.server.card.play.argument.ConsumeModifierArgument;

/**
 * Cards implementing this interface or a child interface are indicated to be modifiers for other cards. By default,
 * modifier cards do not consume moves, move to the discard pile, or log their play when played on their own. It isn't
 * until the target of the ModifierCard plays this card recursively using the {@link ConsumeModifierArgument} that
 * those actions are processed.
 */
public interface ModifierCard
{

}
