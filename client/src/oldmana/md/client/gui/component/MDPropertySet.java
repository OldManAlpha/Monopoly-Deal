package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
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
	
	public int getInterval()
	{
		return GraphicsUtils.getCardWidth() / 3;
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
		PropertyColor effectiveColor = ((PropertySet) getCollection()).getEffectiveColor();
		if (effectiveColor != null && getCollection().getCardCount() > 0 && !(getCollection().getCardCount() == 1 && isCardIncoming()))
		{
			g.setColor(effectiveColor.getColor());
			g.fillRoundRect(0, 0, getWidth(), getHeight() - (isCardIncoming() ? getInterval() : 0), scale(12), scale(12));
			// TODO: Account for cards going into indices not at the end (might require bigger rework)
		}
		g.translate(Math.max(1, scale(1)), Math.max(1, scale(1)));
		for (int i = 0 ; i < getCollection().getCardCount() ; i++)
		{
			if (getCollection().getCardAt(i) != getIncomingCard())
			{
				g.translate(0, (i * getInterval()));
				g.drawImage(getCollection().getCardAt(i).getGraphics(getScale()), 0, 0, GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight(), null);
				g.translate(0, -(i * getInterval()));
			}
		}
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.CYAN);
			GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(16), getWidth(), getHeight());
		}
	}

	@Override
	public Point getLocationInComponentOf(Card card)
	{
		return new Point(1, 1 + (getInterval() * getCollection().getIndexOf(card)));
	}
	
	public class MDPropertySetListener extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (getClient().canActFreely() && !getClient().isInputBlocked())
			{
				if (getCollection().getOwner() == getClient().getThePlayer())
				{
					getClient().getGameState().setCurrentClientActionState(new ActionStateClientModifyPropertySet((PropertySet) getCollection()));
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
