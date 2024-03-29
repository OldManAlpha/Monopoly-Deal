package oldmana.md.client.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.action.ActionScreenRent;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.state.TargetState;
import oldmana.md.common.net.packet.client.action.PacketActionPay;

public class ActionStateRent extends ActionState
{
	private Map<Player, Integer> charges;
	
	private ActionScreenRent rentScreen;
	
	public ActionStateRent(Player renter, Map<Player, Integer> charges)
	{
		super(renter, new ArrayList<Player>(charges.keySet()));
		this.charges = charges;
	}
	
	public ActionScreenRent getRentScreen()
	{
		return rentScreen;
	}
	
	@Override
	public void setTargetState(Player player, TargetState state)
	{
		super.setTargetState(player, state);
		Player thePlayer = getClient().getThePlayer();
		if (thePlayer == player && isTarget(player))
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			if (state == TargetState.REFUSED)
			{
				button.setEnabled(false);
			}
			else if (state == TargetState.ACCEPTED)
			{
				removeButton();
			}
			else if (state == TargetState.TARGETED)
			{
				button.setEnabled(true);
			}
		}
	}
	
	@Override
	protected void evaluateAcceptButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			super.evaluateAcceptButton();
		}
	}
	
	@Override
	public void onPreTargetRemoved(Player player)
	{
		if (player == getClient().getThePlayer())
		{
			cleanup();
		}
		else
		{
			super.onPreTargetRemoved(player);
		}
	}
	
	@Override
	public void applyButtonAccept()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			super.applyButtonAccept();
			return;
		}
		MDButton button = getClient().getTableScreen().getMultiButton();
		button.setEnabled(true);
		button.setText("View Charge");
		button.setColor(ButtonColorScheme.ALERT);
		button.setListener(() ->
		{
			if (button.isEnabled())
			{
				rentScreen.setVisible(true);
			}
		});
	}
	
	@Override
	public void setup()
	{
		Player player = getClient().getThePlayer();
		if (isTarget(player) && !isAccepted(player))
		{
			rentScreen = new ActionScreenRent(getClient().getThePlayer(), this);
			rentScreen.setVisible(false);
			getClient().getTableScreen().setActionScreen(rentScreen);
			
			applyButtonAccept();
		}
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		getClient().getTableScreen().removeActionScreen();
	}
	
	@Override
	public void updateUI()
	{
		rentScreen = new ActionScreenRent(getClient().getThePlayer(), this);
		rentScreen.setVisible(false);
		getClient().getTableScreen().setActionScreen(rentScreen);
	}
	
	public void payRent(List<Card> cards)
	{
		int[] ids = new int[cards.size()];
		for (int i = 0 ; i < ids.length ; i++)
		{
			ids[i] = cards.get(i).getID();
		}
		getClient().sendPacket(new PacketActionPay(ids));
		cleanup();
	}
	
	public int getPlayerRent(Player player)
	{
		return charges.get(player);
	}
}
