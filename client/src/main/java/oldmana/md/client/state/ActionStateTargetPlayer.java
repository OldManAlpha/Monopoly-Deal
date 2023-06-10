package oldmana.md.client.state;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.HashMap;
import java.util.Map;

import oldmana.md.client.Player;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.large.MDPlayer;
import oldmana.md.common.net.packet.client.action.PacketActionSelectPlayer;

public class ActionStateTargetPlayer extends ActionState
{
	private Map<Player, MDSelection> selects = new HashMap<Player, MDSelection>();
	
	private boolean allowSelf;
	
	private Player selectedPlayer;
	private MDSelection selectedPlayerUI;
	
	public ActionStateTargetPlayer(Player player, boolean allowSelf)
	{
		super(player);
		this.allowSelf = allowSelf;
	}
	
	@Override
	public void updateUI()
	{
		selects.forEach((player, select) -> select.setSize(player.getUI().getSize()));
	}
	
	@Override
	public void setup()
	{
		Player player = getActionOwner();
		if (player == getClient().getThePlayer())
		{
			for (Player other : getClient().getAllPlayers())
			{
				if (other == player && !allowSelf)
				{
					continue;
				}
				MDPlayer ui = other.getUI();
				MDSelection select = new MDSelection();
				select.setSize(other.getUI().getSize());
				select.addClickListener(() ->
				{
					if (selectedPlayer != null)
					{
						selectedPlayerUI.setColor(MDSelection.DEFAULT_COLOR);
					}
					selectedPlayer = other;
					selectedPlayerUI = select;
					selectedPlayerUI.setColor(Color.BLUE);
					updateButton();
				});
				
				ComponentAdapter resizeListener = new ComponentAdapter()
				{
					@Override
					public void componentResized(ComponentEvent e)
					{
						select.setSize(ui.getSize());
					}
				};
				ContainerAdapter removeListener = new ContainerAdapter()
				{
					@Override
					public void componentRemoved(ContainerEvent e)
					{
						ui.removeComponentListener(resizeListener);
						ui.removeContainerListener(this);
					}
				};
				ui.addComponentListener(resizeListener);
				ui.addContainerListener(removeListener);
				ui.add(select, 0);
				selects.put(other, select);
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
				button.setListener(() ->
				{
					getClient().sendPacket(new PacketActionSelectPlayer(selectedPlayer.getID()));
					cleanup();
					getClient().setAwaitingResponse(true);
					button.setEnabled(false);
					button.removeListener();
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
		selects.forEach((player, select) -> player.getUI().remove(select));
		getClient().getTableScreen().repaint();
	}
}
