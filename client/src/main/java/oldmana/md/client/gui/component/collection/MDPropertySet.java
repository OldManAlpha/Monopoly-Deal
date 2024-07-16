package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import oldmana.md.client.MDClient;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.Card;
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
import oldmana.md.client.state.client.ActionStateClientDragSetCard;

public class MDPropertySet extends MDCardCollection
{
	private MDCardView view;
	private MDSelection select;
	private Runnable selectTask;
	private double hoverPos;
	
	public MDPropertySet(PropertySet set)
	{
		super(set);
		MDPropertySetListener listener = new MDPropertySetListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
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
		int outlineWidth = getOutlineWidth() * 2;
		setSize(GraphicsUtils.getCardWidth() + outlineWidth, GraphicsUtils.getCardHeight() +
				(getInterval() * (getCollection().getCardCount() - 1 + (isCardBeingRemoved() ? 1 : 0))) + outlineWidth);
		updateGraphics();
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
				onMouseEntered(event.getX(), event.getY());
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
		return Math.min(scale(19), (getMaxHeight() - GraphicsUtils.getCardHeight() - (getOutlineWidth() * 2)) / Math.max(1, cardCount - 1));
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
	
	public void onMouseEntered(int x, int y)
	{
		onMouseExited();
		PropertySet set = (PropertySet) getCollection();
		view = new MDCardView(set.getCards(), set.getEffectiveColor() != null ? set.getEffectiveColor().getColor() : Color.GRAY);
		getClient().addTableComponent(view, 95);
		view.setLocation(SwingUtilities.convertPoint(this, new Point(-(view.getWidth() / 2) + (GraphicsUtils.getCardWidth() / 2), 
				set.getOwner() instanceof ThePlayer ? scale(-200) : scale(100)), getClient().getTableScreen()));
		hoverPos = y / (double) getHeight();
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
	
	public Rectangle getBorderBounds()
	{
		Entry<Card, Point> top = null;
		Entry<Card, Point> bottom = null;
		for (Entry<Card, Point> entry : getCurrentCardPositions().entrySet())
		{
			if (top == null)
			{
				top = entry;
				bottom = entry;
				continue;
			}
			Point pos = entry.getValue();
			if (pos.y < top.getValue().y)
			{
				top = entry;
			}
			if (pos.y > bottom.getValue().y)
			{
				bottom = entry;
			}
		}
		if (top == null)
		{
			return new Rectangle(0, 0, 0, 0);
		}
		return new Rectangle(0, top.getValue().y - getOutlineWidth(), GraphicsUtils.getCardWidth() + (getOutlineWidth() * 2),
				(bottom.getValue().y - top.getValue().y) + GraphicsUtils.getCardHeight() + (getOutlineWidth() * 2));
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		PropertySet set = (PropertySet) getCollection();
		PropertyColor effectiveColor = set.getEffectiveColor();
		if (effectiveColor != null && set.getCardCount() > 0 && !(set.getCardCount() == 1 && isCardIncoming()))
		{
			g.setColor(effectiveColor.getColor());
			Rectangle border = getBorderBounds();
			g.fillRoundRect(0, border.y, border.width, border.height, scale(12), scale(12));
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
		private ActionStateClientDragSetCard state;
		private MDCard move;
		
		@Override
		public void mouseDragged(MouseEvent event)
		{
			if (move != null)
			{
				Point p = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), MDClient.getInstance().getTableScreen());
				move.setLocationCentered(p.getX(), p.getY());
				return;
			}
			if (!getClient().canModifySets() || getClient().isInputBlocked() || getCollection().getOwner() != getClient().getThePlayer())
			{
				
				return;
			}
			if (move == null)
			{
				for (int i = getCardCount() - 1 ; i >= 0  ; i--)
				{
					if (getLocationOf(i).getY() < event.getY())
					{
						move = new MDCard(getCollection().getCardAt(i))
						{
							@Override
							public void doPaint(Graphics gr)
							{
								super.doPaint(gr);
								gr.setColor(new Color(0, 0, 0, 40));
								gr.fillRoundRect(0, 0, getWidth(), getHeight(), scale(5), scale(5));
							}
						};
						Point p = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), MDClient.getInstance().getTableScreen());
						move.setLocationCentered(p.getX(), p.getY());
						MDClient.getInstance().addTableComponent(move, 100);
						
						getClient().getGameState().setClientActionState(
								state = new ActionStateClientDragSetCard((PropertySet) getCollection(), move, () ->
						{
							state = null;
							move = null;
						}));
						break;
					}
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (move != null)
			{
				Point loc = SwingUtilities.convertPoint(getClient().getTableScreen(), move.getCenterX(), move.getCenterY(), getParent());
				Component component = getParent().getComponentAt(loc);
				if (component == null)
				{
					loc = SwingUtilities.convertPoint(getClient().getTableScreen(), move.getCenterX(), move.getCenterY(), getParent().getParent());
					component = getParent().getParent().getComponentAt(loc);
				}
				
				state.cardDropped(component);
				
				MDClient.getInstance().getTableScreen().repaint();
				return;
			}
			if (event.getX() >= 0 && event.getX() < getWidth() && event.getY() >= 0 && event.getY() < getHeight())
			{
				if (getClient().canModifySets() && !getClient().isInputBlocked())
				{
					if (getCollection().getOwner() == getClient().getThePlayer())
					{
						getClient().getGameState().setClientActionState(new ActionStateClientModifyPropertySet((PropertySet) getCollection()));
					}
				}
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent event)
		{
			onMouseEntered(event.getX(), event.getY());
		}
		
		@Override
		public void mouseExited(MouseEvent event)
		{
			onMouseExited();
		}
	}
}
