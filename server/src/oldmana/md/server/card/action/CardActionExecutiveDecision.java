package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;

public class CardActionExecutiveDecision extends CardAction
{
	public CardActionExecutiveDecision()
	{
		super(1, "Executive Decision");
		setDisplayName("EXECUTIVE", "DECISION");
		setFontSize(6);
		setDisplayOffsetY(2);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().nextTurn();
		getServer().getGameState().nextNaturalActionState();
	}
}
