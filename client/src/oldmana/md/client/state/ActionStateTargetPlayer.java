package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.Player;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.net.packet.client.action.PacketActionSelectPlayer;

public class ActionStateTargetPlayer extends ActionState
{
	private List<MDSelection> selects = new ArrayList<MDSelection>();
	
	private Player selectedPlayer;
	private MDSelection selectedPlayerUI;
	
	public ActionStateTargetPlayer(Player player)
	{
		super(player);
	}
	
	@Override
	public void setup()
	{
		Player player = getActionOwner();
		if (player == getClient().getThePlayer())
		{
			for (Player other : getClient().getOtherPlayers())
			{
				MDSelection select = new MDSelection();
				select.setLocation(other.getUI().getLocation());
				select.setSize(other.getUI().getSize());
				select.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						if (selectedPlayer != null)
						{
							selectedPlayerUI.setColor(MDSelection.DEFAULT_COLOR);
						}
						selectedPlayer = other;
						selectedPlayerUI = select;
						selectedPlayerUI.setColor(Color.BLUE);
						updateButton();
					}
				});
				getClient().addTableComponent(select, 100);
				selects.add(select);
			}
		}
	}
	
	public void updateButton()
	{
		if (getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (selectedPlayer != null)
			{
				button.setEnabled(true);
				button.setListener(new MouseAdapter()
				{
					@Override
					public void mouseReleased(MouseEvent event)
					{
						getClient().sendPacket(new PacketActionSelectPlayer(selectedPlayer.getID()));
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
		for (MDSelection select : selects)
		{
			getClient().removeTableComponent(select);
		}
	}
}
