package oldmana.general.md.client.gui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.card.Card;
import oldmana.general.md.net.packet.client.action.PacketActionUndoCard;

public class MDUndoButton extends MDButton
{
	private Card card;
	private MDCardView view;
	
	public MDUndoButton(String text)
	{
		super(text);
		setEnabled(false);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				if (view != null)
				{
					MDClient.getInstance().getTableScreen().remove(view);
					MDClient.getInstance().getTableScreen().repaint();
				}
				if (card != null)
				{
					view = new MDCardView(card);
					view.setLocation(getX() + 24, getY() + 54);
					MDClient.getInstance().addTableComponent(view, 10);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				if (view != null)
				{
					MDClient.getInstance().getTableScreen().remove(view);
					MDClient.getInstance().getTableScreen().repaint();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (isEnabled())
				{
					MDClient.getInstance().sendPacket(new PacketActionUndoCard());
					setEnabled(false);
					if (view != null)
					{
						MDClient.getInstance().getTableScreen().remove(view);
						MDClient.getInstance().getTableScreen().repaint();
					}
				}
			}
		});
	}
	
	public void setUndoCard(Card card)
	{
		this.card = card;
		setEnabled(true);
		repaint();
	}
	
	public void removeUndoCard()
	{
		this.card = null;
		setEnabled(false);
		repaint();
	}
}
