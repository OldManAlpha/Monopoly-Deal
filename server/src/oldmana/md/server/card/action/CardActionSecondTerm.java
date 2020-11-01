package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.GameState;

public class CardActionSecondTerm extends CardAction
{
	public CardActionSecondTerm()
	{
		super(5, "Second Term");
		setDisplayName("SECOND", "TERM");
		setFontSize(8);
		setDisplayOffsetY(3);
		setRevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		GameState gs = getServer().getGameState();
		gs.setTurns(gs.getTurnsRemaining() + 2);
		gs.nextNaturalActionState();
	}
}
