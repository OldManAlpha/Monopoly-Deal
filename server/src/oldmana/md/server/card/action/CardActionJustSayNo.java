package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardSpecial;
import oldmana.md.server.state.ActionState;

public class CardActionJustSayNo extends CardSpecial
{
	public CardActionJustSayNo()
	{
		super(4, "JUST SAY NO!");
		setDisplayName("JUST", "SAY NO!");
		setDisplayOffsetY(1);
	}
	
	@Override
	public void playCard(Player player, int data)
	{
		Player target = getServer().getPlayerByID(data);
		ActionState state = getServer().getGameState().getCurrentActionState();
		if (state.getActionOwner() == player && state.isTarget(target) && state.getActionTarget(target).isRefused())
		{
			state.setRefused(target, false);
			transfer(getServer().getDiscardPile());
		}
		else if (state.isTarget(player) && !state.getActionTarget(player).isRefused())
		{
			state.setRefused(player, true);
			transfer(getServer().getDiscardPile());
		}
		if (player.getHand().getCardCount() == 0)
		{
			player.clearRevokableCards();
			getServer().getDeck().drawCards(player, 5, 1.2);
		}
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	@Override
	public CardType getType()
	{
		return CardType.JUST_SAY_NO;
	}
	
	@Override
	public String toString()
	{
		return "CardActionJustSayNo (" + getValue() + "M)";
	}
}
