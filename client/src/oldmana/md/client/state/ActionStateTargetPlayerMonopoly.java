package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.Player;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.net.packet.client.action.PacketActionSelectPlayerMonopoly;

public class ActionStateTargetPlayerMonopoly extends ActionState
{
	private PropertySet targetSet;
	
	public ActionStateTargetPlayerMonopoly(Player player)
	{
		super(player);
	}
	
	public void setTargetSet(PropertySet set)
	{
		targetSet = set;
	}
	
	public PropertySet getTargetSet()
	{
		return targetSet;
	}
	
	@Override
	public void setup()
	{
		Player player = getActionOwner();
		if (player == getClient().getThePlayer())
		{
			for (Player other : getClient().getOtherPlayers())
			{
				for (PropertySet set : other.getPropertySets())
				{
					if (set.isMonopoly())
					{
						MDPropertySet ui = (MDPropertySet) set.getUI();
						ui.enableSelection(new Runnable()
						{
							@Override
							public void run()
							{
								if (targetSet != set)
								{
									ui.getSelection().setColor(Color.BLUE);
									if (targetSet != null)
									{
										((MDPropertySet) targetSet.getUI()).getSelection().setColor(MDSelection.DEFAULT_COLOR);
									}
									targetSet = set;
									updateButton();
								}
							}
						});
					}
				}
			}
		}
	}
	
	public void updateButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (targetSet != null)
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(new PacketActionSelectPlayerMonopoly(targetSet.getID()));
						cleanup();
						getClient().setAwaitingResponse(true);
						button.setEnabled(false);
						button.removeListener();
					}
				});
			}
			else
			{
				button.setEnabled(false);
				button.removeListener();
			}
		}
	}
	
	@Override
	public void cleanup()
	{
		for (Player player : getClient().getOtherPlayers())
		{
			for (PropertySet set : player.getPropertySets())
			{
				((MDPropertySet) set.getUI()).disableSelection();
			}
		}
	}
}
