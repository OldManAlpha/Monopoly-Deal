package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardSpecial;
import oldmana.md.server.state.ActionState;

public class CardActionJustSayNo extends CardSpecial
{
	public CardActionJustSayNo()
	{
		super(4, "Just Say No!");
		setDisplayName("JUST", "SAY NO!");
		setDisplayOffsetY(1);
		setDescription("Use to stop an action played against you. Can be played against another Just Say No to cancel it.");
	}
	
	@Override
	public void playCard(Player player, int data)
	{
		Player target = getServer().getPlayerByID(data);
		ActionState state = getServer().getGameState().getActionState();
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
		player.checkEmptyHand();
		if (state.isFinished())
		{
			getServer().getGameState().nextNaturalActionState();
		}
	}
	
	@Override
	public CardType getType()
	{
		return CardType.ACTION_COUNTER;
	}
	
	@Override
	public String toString()
	{
		return "CardActionJustSayNo (" + getValue() + "M)";
	}
}
