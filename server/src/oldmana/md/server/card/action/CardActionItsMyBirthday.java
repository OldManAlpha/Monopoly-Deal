package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateRent;

public class CardActionItsMyBirthday extends CardAction
{
	public CardActionItsMyBirthday()
	{
		super(2, "It's My Birthday");
		setDisplayName("IT'S MY", "BIRTHDAY");
		setFontSize(7);
		setRevocable(false);
		setClearsRevocableCards(true);
		setDescription("Charges all other players 2M.");
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setActionState(new ActionStateRent(player, getServer().getPlayersExcluding(player), 2));
	}
}
