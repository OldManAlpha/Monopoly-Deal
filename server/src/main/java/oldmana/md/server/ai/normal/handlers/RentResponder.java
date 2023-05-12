package oldmana.md.server.ai.normal.handlers;

import oldmana.md.server.ai.PlayerAI.RentCombo;
import oldmana.md.server.ai.normal.BasicStateResponder;
import oldmana.md.server.ai.util.CardSnapshot;
import oldmana.md.server.card.Card;
import oldmana.md.server.state.ActionStateRent;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class RentResponder extends BasicStateResponder<ActionStateRent>
{
	private int rent;
	
	@Override
	public CompletableFuture<Void> handle(ActionStateRent state)
	{
		rent = state.getPlayerRent(getPlayer());
		return compute(getAI().getRentCombosIterator(getPlayer().getAllTableCards(), rent), this::processCombo)
				.thenAccept(combo ->
		{
			state.playerPaid(getPlayer(), combo.getCards());
		});
	}
	
	private RentCombo processCombo(RentCombo combo)
	{
		if (combo.getValue() >= rent)
		{
			return combo;
		}
		return null;
	}
}
