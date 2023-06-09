package oldmana.md.server.history;

import oldmana.md.server.MDServer;
import oldmana.md.server.Player;
import oldmana.md.server.card.Card;

import java.util.Collections;
import java.util.List;

public class BasicUndoableAction implements UndoableAction
{
	private Card card;
	private Player owner;
	private int moveCost;
	
	public BasicUndoableAction(Card card, Player owner, int moveCost)
	{
		this.card = card;
		this.owner = owner;
		this.moveCost = moveCost;
	}
	
	@Override
	public void performUndo()
	{
		MDServer.getInstance().getGameState().incrementMoves(moveCost);
		card.transfer(owner.getHand());
	}
	
	@Override
	public Card getFace()
	{
		return card;
	}
	
	@Override
	public List<Card> getCards()
	{
		return Collections.singletonList(card);
	}
}
