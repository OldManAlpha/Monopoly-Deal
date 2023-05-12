package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardBuilding;
import oldmana.md.client.card.CardButton;
import oldmana.md.client.card.CardButton.CardButtonPosition;
import oldmana.md.client.card.CardButton.CardButtonType;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.client.ActionStateClientPlayBuilding;
import oldmana.md.client.state.client.ActionStateClientPlayProperty;
import oldmana.md.net.packet.client.action.PacketActionUseCardButton;

import javax.swing.SwingUtilities;

public class MDOverlayHand extends MDComponent
{
	private Card card;
	
	private MDCardInfo cardInfo;
	
	private boolean hasButtons;
	
	public MDOverlayHand(Card card)
	{
		this.card = card;
		MDClient client = MDClient.getInstance();
		setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
		Player player = client.getThePlayer();
		
		
		if (getClient().isInputBlocked())
		{
			return;
		}
		
		Map<CardButtonPosition, CardButton> buttons = card.getButtons();
		
		buttons.forEach((pos, button) ->
		{
			MDButton uiButton = new MDButton(button.getText());
			uiButton.setColor(button.getColors());
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
				else if (button.getType() == CardButtonType.BUILDING)
				{
					client.getGameState().setClientActionState(new ActionStateClientPlayBuilding((CardBuilding) card));
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
	
	public void addCardInfo()
	{
		getClient().getScheduler().scheduleTask(task ->
		{
			if (isDisplayable())
			{
				cardInfo = new MDCardInfo(card);
				Point infoPos = SwingUtilities.convertPoint(this, new Point(getWidth() / 2, -cardInfo.getHeight() - scale(5)), getClient().getTableScreen());
				infoPos.x = Math.max(scale(2), Math.min(infoPos.x - (cardInfo.getWidth() / 2), getClient().getTableScreen().getWidth() - cardInfo.getWidth() - scale(2)));
				infoPos.y = Math.max(scale(2), infoPos.y);
				cardInfo.setLocation(infoPos.x, infoPos.y);
				cardInfo.setCardPos((int) (((MDHand) getClient().getThePlayer().getHand().getUI()).getScreenLocationOf(card).getX()
						- cardInfo.getX() + GraphicsUtils.getCardWidth()));
				getClient().addTableComponent(cardInfo, 110);
			}
		}, 250, false);
	}
	
	public void removeCardInfo()
	{
		if (cardInfo != null)
		{
			getClient().removeTableComponent(cardInfo);
			getClient().getTableScreen().repaint();
			cardInfo = null;
		}
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
