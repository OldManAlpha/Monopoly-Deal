package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.component.MDCard;
import oldmana.md.client.gui.component.MDCardView;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;
import oldmana.md.client.state.client.ActionStateClientModifyPropertySet;

public class MDPropertySet extends MDCardCollection
{
	private MDCardView view;
	private MDSelection select;
	private Runnable selectTask;
	
	public MDPropertySet(PropertySet set)
	{
		super(set);
		addMouseListener(new MDPropertySetListener());
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (select != null)
				{
					select.setSize(getSize());
				}
			}
		});
	}
	
	@Override
	public void update()
	{
		int outlineWidth = (int) Math.max(GraphicsUtils.SCALE + 0.3, 1) * 2;
		setSize(GraphicsUtils.getCardWidth() + outlineWidth, GraphicsUtils.getCardHeight() +
				(getInterval() * (getCollection().getCardCount() - 1 + (isCardBeingRemoved() ? 1 : 0))) + outlineWidth);
		repaint();
	}
	
	@Override
	public void modificationFinished()
	{
		super.modificationFinished();
		update();
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
	
	public double getInterval(int cardCount)
	{
		return (getMaxHeight() - GraphicsUtils.getCardHeight() - (getOutlineWidth() * 2)) / Math.max(2.2, cardCount - 1);
	}
	
	public double getInterval()
	{
		return getInterval(getCollection().getCardCount());
	}
	
	public int getMaxHeight()
	{
		if (getParent() == null)
		{
			return 100;
		}
		return getParent().getHeight();
	}
	
	public void onMouseEntered()
	{
		onMouseExited();
		PropertySet set = (PropertySet) getCollection();
		view = new MDCardView(set.getCards(), set.getEffectiveColor() != null ? set.getEffectiveColor().getColor() : Color.GRAY);
		getClient().addTableComponent(view, 95);
		view.setLocation(SwingUtilities.convertPoint(this, new Point(-(view.getWidth() / 2) + (GraphicsUtils.getCardWidth() / 2), 
				set.getOwner() instanceof ThePlayer ? scale(-200) : scale(100)), getClient().getTableScreen()));
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
			int shiftOffset = 0;
			if (isCardIncoming())
			{
				if (getModIndex() < getCardCount() - 1)
				{
					shiftOffset = (int) -(getInterval() - (getVisibleShiftProgress() * getInterval()));
				}
				else
				{
					shiftOffset = (int) -getInterval();
				}
			}
			else if (isCardBeingRemoved())
			{
				if (getModIndex() < getCardCount())
				{
					shiftOffset = (int) -(getVisibleShiftProgress() * getInterval());
				}
				else
				{
					shiftOffset = (int) -getInterval();
				}
			}
			g.fillRoundRect(0, 0, getWidth(), getHeight() + shiftOffset, scale(12), scale(12));
		}
		if (!(set.getCardCount() == 1 && isCardIncoming()))
		{
			paintCards(g);
		}
		if (set.isMonopoly() && !isBeingModified())
		{
			int drawY = getHeight() - (int) (GraphicsUtils.getCardHeight() * 0.75);
			g.setColor(new Color(255, 215, 0, 100));
			g.fillRect(0, drawY - scale(12), getWidth(), scale(24));
			g.setColor(Color.DARK_GRAY);
			g.drawLine(0, drawY - scale(12), getWidth(), drawY - scale(12));
			g.drawLine(0, drawY + scale(12), getWidth(), drawY + scale(12));
			g.setColor(new Color(255, 215, 0));
			TextPainter.paint(g, "Full Set", GraphicsUtils.getBoldMDFont(scale(16)), 0, drawY - scale(10),
					getWidth(), scale(26), Alignment.CENTER, Alignment.CENTER, Outline.of(Color.DARK_GRAY, scale(4)));
		}
		if (getClient().isDebugEnabled())
		{
			g.setColor(Color.CYAN);
			GraphicsUtils.drawDebug(g, "ID: " + set.getID(), scale(16), getWidth(), (int) (GraphicsUtils.getCardHeight() * 0.5));
		}
	}
	
	public int getOutlineWidth()
	{
		return (int) Math.max(GraphicsUtils.SCALE + 0.3, 1);
	}

	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		int outlineWidth = getOutlineWidth();
		return new Point(outlineWidth, (int) (outlineWidth + (getInterval(cardCount) * cardIndex)));
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
