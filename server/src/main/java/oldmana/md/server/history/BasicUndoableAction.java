package oldmana.md.server.history;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

import java.util.Collections;
import java.util.List;

public class BasicUndoableAction implements UndoableAction
{
	private List<Card> cards;
	private Player owner;
	private int moveCost;
	
	public BasicUndoableAction(Card card, Player owner, int moveCost)
	{
		this(Collections.singletonList(card), owner, moveCost);
	}
	
	public BasicUndoableAction(List<Card> cards, Player owner, int moveCost)
	{
		this.cards = cards;
		this.owner = owner;
		this.moveCost = moveCost;
	}
	
	@Override
	public void performUndo()
	{
		MDServer.getInstance().getGameState().incrementMoves(moveCost);
		double time = Math.max(1 - ((cards.size() - 1) * 0.2), 0.5);
		for (int i = cards.size() - 1 ; i >= 0 ; i--)
		{
			cards.get(i).transfer(owner.getHand(), -1, time);
		}
	}
	
	@Override
	public Card getFace()
	{
		return cards.get(0);
	}
	
	@Override
	public List<Card> getCards()
	{
		return Collections.unmodifiableList(cards);
	}
}
