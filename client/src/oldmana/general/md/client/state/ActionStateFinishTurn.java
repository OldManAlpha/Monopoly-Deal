package oldmana.general.md.client.state;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.general.md.client.Player;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.general.md.net.packet.client.action.PacketActionEndTurn;

public class ActionStateFinishTurn extends ActionState
{
	public ActionStateFinishTurn(Player player)
	{
		super(player);
	}
	
	@Override
	public void setup()
	{
		getGameState().setWhoseTurn(getActionOwner());
		getGameState().setTurns(0);
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setColorScheme(ButtonColorScheme.ALERT);
			button.setText("End Turn");
			if (getActionOwner().getHand().getCardCount() > 7)
			{
				button.setEnabled(false);
			}
			else
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						if (!getClient().isInputBlocked() && getClient().canActFreely() && getActionOwner().getHand().getCardCount() <= 7)
						{
							getClient().sendPacket(new PacketActionEndTurn());
							getClient().setAwaitingResponse(true);
							button.setEnabled(false);
							button.removeListener();
						}
					}
				});
			}
		}
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		/*
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setEnabled(false);
			button.setText("");
			button.removeListener();
		}
		*/
	}
}
