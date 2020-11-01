package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.state.GameState;

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
