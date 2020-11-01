package oldmana.general.md.server.card.action;

import oldmana.general.md.server.Player;
import oldmana.general.md.server.card.CardAction;
import oldmana.general.md.server.state.ActionStateTargetMutualAgreement;

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
