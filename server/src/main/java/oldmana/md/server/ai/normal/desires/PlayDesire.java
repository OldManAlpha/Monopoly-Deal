package oldmana.md.server.ai.normal.desires;

import oldmana.md.server.card.Card;

public interface PlayDesire<T extends Card>
{
	/**
	 * Normal Range 0-100: If the desire is less than 25, it won't be acted on unless required
	 * Less than 0 indicates the card cannot be played under any circumstance
	 */
	double getDesire(T card);
}
