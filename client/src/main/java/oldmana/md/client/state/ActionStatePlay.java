package oldmana.md.client.state;

import oldmana.md.client.Player;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.common.net.packet.client.action.PacketActionEndTurn;

public class ActionStatePlay extends ActionState
{
	private int turns;
	
	public ActionStatePlay(Player player, int turns)
	{
		super(player);
		this.turns = turns;
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
			button.setText("End Turn");
			boolean ignoreMax = getClient().getRules().canDiscardEarly();
			int maxCards = getClient().getRules().getMaxCardsInHand();
			if (!ignoreMax && getActionOwner().getHand().getCardCount() > maxCards)
			{
				button.setEnabled(false);
			}
			else
			{
				button.setEnabled(true);
				button.setListener(() ->
				{
					if (!getClient().isInputBlocked() && getClient().canActFreely() &&
							(ignoreMax || getActionOwner().getHand().getCardCount() <= maxCards))
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
	
	public int getTurns()
	{
		return turns;
	}
}
