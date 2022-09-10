package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.Map;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.card.CardButton.CardButtonPosition;
import oldmana.md.client.card.CardButton.CardButtonType;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.client.ActionStateClientPlayProperty;
import oldmana.md.net.packet.client.action.PacketActionUseCardButton;

public class MDOverlayHand extends MDComponent
{
	private Card card;
	private MDInfoIcon icon;
	
	private boolean hasButtons;
	
	public MDOverlayHand(Card card)
	{
		this.card = card;
		MDClient client = MDClient.getInstance();
		setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
		Player player = client.getThePlayer();
		
		icon = new MDInfoIcon(card);
		icon.setLocation(getWidth() - scale(24), scale(2));
		add(icon);
		
		if (getClient().isInputBlocked())
		{
			return;
		}
		
		Map<CardButtonPosition, CardButton> buttons = card.getButtons();
		
		buttons.forEach((pos, button) ->
		{
			MDButton uiButton = new MDButton(button.getText());
			uiButton.setColorScheme(button.getColors());
			uiButton.setSize((int) (getWidth() * 0.8), (int) (getHeight() * 0.2));
			uiButton.setLocationCentered((int) (getWidth() * 0.5), (int) (getHeight() * pos.getLocation()));
			uiButton.addClickListener(() ->
			{
				if (button.getType() == CardButtonType.NORMAL)
				{
					getClient().sendPacket(new PacketActionUseCardButton(card.getID(), pos.getID(), 0));
					client.setAwaitingResponse(true);
				}
				else if (button.getType() == CardButtonType.PROPERTY)
				{
					CardProperty prop = (CardProperty) card;
					if (prop.isSingleColor() || !player.hasCompatiblePropertySetWithRoom(prop))
					{
						client.sendPacket(new PacketActionUseCardButton(prop.getID(), button.getPosition().getID(), -1));
						//client.sendPacket(new PacketActionPlayCardProperty(prop.getID(), -1));
						client.setAwaitingResponse(true);
					}
					else
					{
						client.getGameState().setClientActionState(new ActionStateClientPlayProperty(prop, button));
					}
				}
				else if (button.getType() == CardButtonType.ACTION_COUNTER)
				{
					ActionState state = client.getGameState().getActionState();
					state.onActionCounter(card, button);
				}
				Container parent = getParent();
				if (parent != null)
				{
					((MDHand) parent).removeOverlay();
				}
			});
			add(uiButton);
			hasButtons = true;
		});
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public void removeCardInfo()
	{
		icon.removeCardInfo();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.drawImage(card.getGraphics(getScale() * 2), 0, 0, null);
		if (hasButtons)
		{
			g.setColor(new Color(0, 0, 0, 60));
			g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(20), scale(20));
		}
	}
}
