package oldmana.md.client.state;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardActionJustSayNo;
import oldmana.md.client.gui.action.ActionScreenRent;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.md.net.packet.client.action.PacketActionPay;

public class ActionStateRent extends ActionState
{
	private int amount;
	
	private ActionScreenRent rentScreen;
	
	public ActionStateRent(Player renter, Player rented, int amount)
	{
		super(renter, rented, false);
		this.amount = amount;
	}
	
	public ActionStateRent(Player renter, List<Player> rented, int amount)
	{
		super(renter, rented, false);
		this.amount = amount;
	}
	
	@Override
	public void onPreTargetRemoved(Player player)
	{
		if (player == getClient().getThePlayer())
		{
			cleanup();
		}
	}
	
	@Override
	public void onJustSayNo(CardActionJustSayNo jsn)
	{
		super.onJustSayNo(jsn);
	}
	
	@Override
	public void setRefused(Player player, boolean refused)
	{
		super.setRefused(player, refused);
		Player thePlayer = getClient().getThePlayer();
		if (thePlayer == player && isTarget(player))
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			if (refused)
			{
				button.setEnabled(false);
			}
			else
			{
				button.setEnabled(true);
			}
		}
	}
	
	@Override
	public void setAccepted(Player player, boolean accepted)
	{
		super.setAccepted(player, accepted);
		Player thePlayer = getClient().getThePlayer();
		if (thePlayer == player && isTarget(player))
		{
			if (accepted)
			{
				cleanup();
			}
		}
	}
	
	@Override
	public void updateOverlaysAndButton()
	{
		super.updateOverlaysAndButton();
	}
	
	@Override
	public void setup()
	{
		Player player = getClient().getThePlayer();
		if (isTarget(player) && !isAccepted(player))
		{
			rentScreen = new ActionScreenRent(getClient().getThePlayer(), this);
			rentScreen.setVisible(false);
			getClient().addTableComponent(rentScreen, 110);
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setEnabled(true);
			button.setText("View Rent");
			button.setColorScheme(ButtonColorScheme.ALERT);
			button.repaint();
			button.setListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					if (button.isEnabled())
					{
						rentScreen.setVisible(true);
					}
				}
			});
		}
		else if (getActionOwner() == player)
		{
			
		}
	}
	
	@Override
	public void cleanup()
	{
		super.cleanup();
		if (rentScreen != null)
		{
			getClient().removeTableComponent(rentScreen);
			getClient().getTableScreen().repaint();
			rentScreen = null;
		}
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
	
	public void rebuildRentScreen()
	{
		if (rentScreen != null)
		{
			getClient().removeTableComponent(rentScreen);
		}
		rentScreen = new ActionScreenRent(getClient().getThePlayer(), this);
		rentScreen.setVisible(false);
		getClient().addTableComponent(rentScreen, 110);
	}
	
	public int getRent()
	{
		return amount;
	}
}
