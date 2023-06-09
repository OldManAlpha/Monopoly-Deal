package oldmana.md.server.history;

import oldmana.md.server.card.Card;

import java.util.List;

public interface UndoableAction
{
	/**
	 * Undo the action, restoring state to what it was before the action was performed.
	 */
	void performUndo();
	
	/**
	 * Returns the visual representation of this UndoableAction.
	 * @return The Card that represents this
	 */
	Card getFace();
	
	/**
	 * Get the cards involved in this action.
	 * @return The List of cards
	 */
	List<Card> getCards();
	
	/**
	 * Check if the card is involved in this action.
	 * @param card The card to check
	 * @return True if the card is contained in the action
	 */
	default boolean hasCard(Card card)
	{
		return getCards().contains(card);
	}
}
