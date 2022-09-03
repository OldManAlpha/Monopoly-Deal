package oldmana.md.client.state.client;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.ActionState;
import oldmana.md.net.packet.client.action.PacketActionUseCardButton;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ActionStateClientCounterPlayer extends ActionStateClient
{
	private Card card;
	private CardButton cardButton;
	
	private List<MDComponent> cardSelect;
	private List<MDSelection> selects;
	private MDButton cancel;
	
	private Player selectedPlayer;
	private MDSelection selectedPlayerUI;
	
	
	public ActionStateClientCounterPlayer(Card card, CardButton cardButton)
	{
		this.card = card;
		this.cardButton = cardButton;
	}
	
	@Override
	public void setup()
	{
		MDHand hand = ((MDHand) getClient().getThePlayer().getHand().getUI());
		cardSelect = hand.placeSelectedView(card, 90, Color.BLUE);
		
		int width = GraphicsUtils.getCardWidth(2);
		int height = GraphicsUtils.getCardHeight(2);
		Point loc = hand.getScreenLocationOf(card);
		cancel = new MDButton("Cancel");
		cancel.setSize((int) (width * 0.8), (int) (height * 0.2));
		cancel.setLocation((int) (width * 0.1) + loc.getX(), (int) (height * 0.4) + loc.getY());
		cancel.setListener(() -> removeState());
		getClient().addTableComponent(cancel, 92);
		
		ActionState state = getGameState().getActionState();
		selects = new ArrayList<MDSelection>();
		for (Player other : state.getRefused())
		{
			MDSelection select = new MDSelection();
			select.setLocation(other.getUI().getLocation());
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
			getClient().addTableComponent(select, 100);
			selects.add(select);
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
					getClient().sendPacket(new PacketActionUseCardButton(card.getID(),
							cardButton.getPosition().getID(), selectedPlayer.getID()));
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
		getClient().removeTableComponents(cardSelect);
		getClient().removeTableComponents(selects);
		getClient().removeTableComponent(cancel);
		getClient().getTableScreen().repaint();
	}
}
