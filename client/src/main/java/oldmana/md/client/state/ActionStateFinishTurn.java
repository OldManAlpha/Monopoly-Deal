package oldmana.md.client.state;

import oldmana.md.client.Player;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.net.packet.client.action.PacketActionEndTurn;

public class ActionStateFinishTurn extends ActionState
{
	public ActionStateFinishTurn(Player player)
	{
		super(player);
	}
	
	@Override
	public boolean isTurnState()
	{
		return true;
	}
	
	@Override
	public void setup()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setColor(ButtonColorScheme.ALERT);
			button.setText("End Turn");
			int maxCards = getClient().getRules().getMaxCardsInHand();
			if (getActionOwner().getHand().getCardCount() > maxCards)
			{
				button.setEnabled(false);
			}
			else
			{
				button.setEnabled(true);
				button.setListener(() ->
				{
					if (!getClient().isInputBlocked() && getClient().canActFreely() &&
							getActionOwner().getHand().getCardCount() <= maxCards)
					{
						getClient().sendPacket(new PacketActionEndTurn());
						getClient().setAwaitingResponse(true);
						button.setEnabled(false);
						button.removeListener();
					}
				});
			}
		}
	}
}
