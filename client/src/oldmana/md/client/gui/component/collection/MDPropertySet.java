package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCardView;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.client.ActionStateClientModifyPropertySet;

public class MDPropertySet extends MDCardCollection
{
	public static Dimension PROPERTY_SET_SIZE = new Dimension(MDCard.CARD_SIZE.width + 2, MDCard.CARD_SIZE.height + MDCard.CARD_SIZE.width + 2);
	
	private MDCardView view;
	private MDSelection select;
	private Runnable selectTask;
	
	public MDPropertySet(PropertySet set)
	{
		super(set);
		setSize(PROPERTY_SET_SIZE);
		addMouseListener(new MDPropertySetListener());
	}
	
	@Override
	public void update()
	{
		// 2.25 x 3.5
		setSize(GraphicsUtils.getCardWidth() + Math.max(2, scale(1) * 2), GraphicsUtils.getCardHeight() + (getInterval() * (getCollection().getCardCount() - 1)) + Math.max(2, scale(1) * 2));
		repaint();
	}
	
	public void enableSelection()
	{
		select = new MDSelection();
		select.setSize(getWidth(), getHeight());
		add(select);
	}
	
	public void enableSelection(Color color)
	{
		select = new MDSelection();
		select.setSize(getWidth(), getHeight());
		select.setColor(color);
		add(select);
	}
	
	public void enableSelection(Runnable task)
	{
		select = new MDSelection();
		select.setSize(getWidth(), getHeight());
		selectTask = task;
		select.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				task.run();
				onMouseExited();
			}
			
			@Override
			public void mouseEntered(MouseEvent event)
			{
				onMouseEntered();
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				onMouseExited();
			}
		});
		add(select);
	}
	
	public void disableSelection()
	{
		if (select != null)
		{
			remove(select);
		}
		selectTask = null;
	}
	
	public MDSelection getSelection()
	{
		return select;
	}
	
	public int getInterval(int cardCount)
	{
		return GraphicsUtils.getCardWidth() / Math.max(3, cardCount - 1);
	}
	
	public int getInterval()
	{
		return getInterval(getCollection().getCardCount());
	}
	
	public void onMouseEntered()
	{
		onMouseExited();
		PropertySet set = (PropertySet) getCollection();
		view = new MDCardView(set.getCards(), set.getEffectiveColor() != null ? set.getEffectiveColor().getColor() : Color.GRAY);
		getClient().addTableComponent(view, 95);
		view.setLocation(SwingUtilities.convertPoint(this, new Point(-(view.getWidth() / 2) + (GraphicsUtils.getCardWidth() / 2), 
				set.getOwner().getUIPosition() > 1 ? scale(-200) : scale(100)), getClient().getTableScreen()));
	}
	
	public void onMouseExited()
	{
		if (view != null)
		{
			view.getParent().repaint();
			view.getParent().remove(view);
			view = null;
		}
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		PropertySet set = (PropertySet) getCollection();
		PropertyColor effectiveColor = set.getEffectiveColor();
		if (effectiveColor != null && set.getCardCount() > 0 && !(set.getCardCount() == 1 && isCardIncoming()))
		{
			g.setColor(effectiveColor.getColor());
			g.fillRoundRect(0, 0, getWidth(), getHeight() - (isCardIncoming() ? getInterval() : 0), scale(12), scale(12));
			// TODO: Account for cards going into indices not at the end (might require bigger rework)
		}
		if (!(set.getCardCount() == 1 && isCardIncoming()))
		{
			paintCards(g);
		}
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.CYAN);
			GraphicsUtils.drawDebug(g, "ID: " + set.getID(), scale(16), getWidth(), (int) (GraphicsUtils.getCardHeight() * 0.5));
		}
	}

	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(Math.max(1, scale(1)), Math.max(1, scale(1)) + (getInterval(cardCount) * cardIndex));
	}
	
	public class MDPropertySetListener extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (getClient().canModifySets() && !getClient().isInputBlocked())
			{
				if (getCollection().getOwner() == getClient().getThePlayer())
				{
					getClient().getGameState().setClientActionState(new ActionStateClientModifyPropertySet((PropertySet) getCollection()));
				}
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent event)
		{
			onMouseEntered();
		}
		
		@Override
		public void mouseExited(MouseEvent event)
		{
			onMouseExited();
		}
	}
}
