package oldmana.md.client.state.client;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.large.MDPlayer;
import oldmana.md.client.state.ActionState;
import oldmana.md.common.net.packet.client.action.PacketActionUseCardButton;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.HashMap;
import java.util.Map;

public class ActionStateClientCounterPlayer extends ActionStateClient
{
	private Card card;
	private CardButton cardButton;
	
	private HandCardSelection cardSelection;
	private Map<Player, MDSelection> selects;
	
	private Player selectedPlayer;
	private MDSelection selectedPlayerUI;
	
	
	public ActionStateClientCounterPlayer(Card card, CardButton cardButton)
	{
		this.card = card;
		this.cardButton = cardButton;
	}
	
	@Override
	public void updateUI()
	{
		cardSelection.destroy();
		cardSelection.create(() -> removeState());
		selects.forEach((player, select) -> select.setSize(player.getUI().getSize()));
	}
	
	@Override
	public void setup()
	{
		cardSelection = new HandCardSelection(card);
		cardSelection.create(() -> removeState());
		
		ActionState state = getGameState().getActionState();
		selects = new HashMap<Player, MDSelection>();
		for (Player other : state.getRefused())
		{
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
		updateButton();
	}
	
	public void updateButton()
	{
		if (getGameState().getActionState().getActionOwner() == getClient().getThePlayer())
		{
			MDButton button = getClient().getTableScreen().getMultiButton();
			button.setText("Confirm");
			if (selectedPlayer != null)
			{
				button.setEnabled(true);
				button.setListener(() ->
				{
					getClient().sendPacket(new PacketActionUseCardButton(card.getID(), cardButton.getID(), selectedPlayer.getID()));
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
			button.repaint();
		}
	}
	
	@Override
	public void cleanup()
	{
		getGameState().getActionState().removeActionCounter();
		cardSelection.destroy();
		selects.forEach((player, select) -> player.getUI().remove(select));
		getClient().getTableScreen().repaint();
	}
}
