package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;

public class CardActionFilibuster extends CardAction
{
	public CardActionFilibuster()
	{
		super(4, "Filibuster");
		setDisplayName("FILIBUSTER");
		setFontSize(6);
		setDisplayOffsetY(2);
		setRevocable(false);
		setMarksPreviousUnrevocable(false);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().deferWinBy(3);
	}
}
