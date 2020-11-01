package oldmana.md.server.card.action;

import oldmana.md.server.Player;
import oldmana.md.server.card.CardAction;
import oldmana.md.server.state.ActionStateTargetMutualAgreement;

public class CardActionMutualAgreement extends CardAction
{
	public CardActionMutualAgreement()
	{
		super(1, "Mutual Agreement");
		setDisplayName("MUTUAL", "AGREEMENT");
		setFontSize(6);
		setDisplayOffsetY(1);
	}
	
	@Override
	public void playCard(Player player)
	{
		getServer().getGameState().setCurrentActionState(new ActionStateTargetMutualAgreement(player));
	}
}
