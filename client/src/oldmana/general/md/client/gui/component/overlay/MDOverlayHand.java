package oldmana.general.md.client.gui.component.overlay;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.card.Card;
import oldmana.general.md.client.card.CardAction;
import oldmana.general.md.client.card.CardActionDoubleTheRent;
import oldmana.general.md.client.card.CardActionJustSayNo;
import oldmana.general.md.client.card.CardActionRent;
import oldmana.general.md.client.card.CardMoney;
import oldmana.general.md.client.card.CardProperty;
import oldmana.general.md.client.gui.component.MDButton;
import oldmana.general.md.client.gui.component.MDCard;
import oldmana.general.md.client.gui.component.MDComponent;
import oldmana.general.md.client.gui.component.large.MDHand;
import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.state.ActionState;
import oldmana.general.md.client.state.ActionStateDiscard;
import oldmana.general.md.client.state.client.ActionStateClientPlayProperty;
import oldmana.general.md.net.packet.client.action.PacketActionDiscard;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardAction;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardBank;
import oldmana.general.md.net.packet.client.action.PacketActionPlayCardProperty;
import oldmana.general.md.net.packet.client.action.PacketActionPlayMultiCardAction;

public class MDOverlayHand extends MDComponent
{
	private Card card;
	private MDButton playButton;
	private MDButton bankButton;
	
	public MDOverlayHand(Card card)
	{
		super();
		this.card = card;
		MDClient client = MDClient.getInstance();
		setSize(GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2));
		ActionState state = client.getGameState().getCurrentActionState();
		if (state instanceof ActionStateDiscard && state.getActionOwner() == client.getThePlayer())
		{
			MDButton discard = new MDButton("Discard");
			discard.setSize((int) (getWidth() * 0.8), (int) (getHeight() * 0.2));
			discard.setLocation((int) (getWidth() * 0.1), (int) (getHeight() * 0.4));
			discard.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					client.sendPacket(new PacketActionDiscard(card.getID()));
					client.setAwaitingResponse(true);
				}
			});
			add(discard);
		}
		else
		{
			playButton = new MDButton("Play");
			playButton.setSize((int) (getWidth() * 0.8), (int) (getHeight() * 0.2));
			playButton.setLocation((int) (getWidth() * 0.1), (int) (getHeight() * 0.15));
			playButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					if (card instanceof CardActionJustSayNo)
					{
						ActionState state = client.getGameState().getCurrentActionState();
						state.onJustSayNo((CardActionJustSayNo) card);
					}
					else if (card instanceof CardAction)
					{
						client.sendPacket(new PacketActionPlayCardAction(card.getID()));
						client.setAwaitingResponse(true);
					}
					else if (card instanceof CardProperty)
					{
						CardProperty prop = (CardProperty) card;
						if (prop.isSingleColor())
						{
							client.sendPacket(new PacketActionPlayCardProperty(prop.getID(), -1));
							client.setAwaitingResponse(true);
						}
						else if (client.getThePlayer().hasCompatiblePropertySetWithRoom(prop))
						{
							client.getGameState().setCurrentClientActionState(new ActionStateClientPlayProperty(prop));
						}
						else
						{
							client.sendPacket(new PacketActionPlayCardProperty(prop.getID(), -1));
							client.setAwaitingResponse(true);
						}
					}
					Container parent = MDOverlayHand.this.getParent();
					if (parent != null)
					{
						((MDHand) parent).removeOverlay();
					}
				}
			});
			
			MDButton doubleButton = new MDButton("Double Rent");
			doubleButton.setSize((int) (getWidth() * 0.8), (int) (getHeight() * 0.2));
			doubleButton.setLocation((int) (getWidth() * 0.1), (int) (getHeight() * 0.4));
			doubleButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					CardActionDoubleTheRent doubleRent = client.getThePlayer().getFirstDoubleTheRent();
					client.sendPacket(new PacketActionPlayMultiCardAction(new int[] {card.getID(), doubleRent.getID()}));
					client.setAwaitingResponse(true);
					Container parent = MDOverlayHand.this.getParent();
					if (parent != null)
					{
						((MDHand) parent).removeOverlay();
					}
				}
			});
			
			bankButton = new MDButton("Bank");
			bankButton.setSize((int) (getWidth() * 0.8), (int) (getHeight() * 0.2));
			bankButton.setLocation((int) (getWidth() * 0.1), (int) (getHeight() * 0.65));
			bankButton.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseReleased(MouseEvent event)
				{
					client.sendPacket(new PacketActionPlayCardBank(card.getID()));
					client.setAwaitingResponse(true);
					Container parent = MDOverlayHand.this.getParent();
					if (parent != null)
					{
						((MDHand) parent).removeOverlay();
					}
				}
			});
			if (!(card instanceof CardMoney || card instanceof CardActionJustSayNo || card instanceof CardActionDoubleTheRent) 
					|| (card instanceof CardActionJustSayNo && !client.canActFreely()))
			{
				add(playButton);
			}
			if (card instanceof CardActionRent && client.getThePlayer().hasDoubleTheRent() && client.getGameState().getTurns() > 1 && client.canActFreely())
			{
				add(doubleButton);
			}
			if (!(card instanceof CardProperty) && client.canActFreely())
			{
				add(bankButton);
			}
		}
	}
	
	public Card getCard()
	{
		return card;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 60));
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(20), scale(20));
	}
}
