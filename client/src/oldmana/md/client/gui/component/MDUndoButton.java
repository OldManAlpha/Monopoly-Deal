package oldmana.md.client.gui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.card.Card;
import oldmana.md.net.packet.client.action.PacketActionUndoCard;

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
					getClient().removeTableComponent(view);
					getClient().getTableScreen().repaint();
				}
				if (card != null)
				{
					view = new MDCardView(card);
					view.setLocation(getX() + (getWidth() / 2) - (view.getWidth() / 2), getMaxY() + scale(10));
					getClient().addTableComponent(view, 10);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				if (view != null)
				{
					getClient().removeTableComponent(view);
					getClient().getTableScreen().repaint();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (isEnabled())
				{
					getClient().sendPacket(new PacketActionUndoCard());
					setEnabled(false);
					if (view != null)
					{
						getClient().removeTableComponent(view);
						getClient().getTableScreen().repaint();
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
