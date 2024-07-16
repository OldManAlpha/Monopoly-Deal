package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.gui.AutoScrollable;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDPlayerPropertySets extends MDComponent implements AutoScrollable
{
	private Player player; 
	
	private List<MDPropertySet> sets = new ArrayList<MDPropertySet>();
	private MDCreateSet createSet;
	
	private boolean scrollEnabled = true;
	private int scrollPos;
	private int scrollMax;
	
	private MouseWheelListener mouseWheelListener = event ->
	{
		if (scrollEnabled)
		{
			int amt = event.getUnitsToScroll() * scale(5);
			scrollPos = Math.max(0, Math.min(scrollMax, scrollPos + amt));
			invalidate();
			updateGraphics();
			player.getUI().updatePropertySetScrollButtons();
		}
	};
	
	public MDPlayerPropertySets(Player player)
	{
		this.player = player;
		setLayout(new PropertySetsLayout());
	}
	
	public void addPropertySet(MDPropertySet set)
	{
		sets.add(set);
		add(set, 0);
		getParent().invalidate();
	}
	
	public void removePropertySet(MDPropertySet set)
	{
		set.onMouseExited();
		sets.remove(set);
		remove(set);
		getParent().invalidate();
	}
	
	public void addCreateSet(Runnable clickListener)
	{
		removeCreateSet();
		createSet = new MDCreateSet(getClient().getThePlayer());
		createSet.setSize(GraphicsUtils.getCardWidth(), GraphicsUtils.getCardHeight());
		add(createSet);
		createSet.addClickListener(clickListener);
		getParent().invalidate();
	}
	
	public void removeCreateSet()
	{
		if (createSet != null)
		{
			remove(createSet);
			createSet = null;
			getParent().invalidate();
		}
	}
	
	@Override
	public int getScrollNeededToView(MDComponent component)
	{
		MDPropertySet set = (MDPropertySet) component;
		return Math.min(sets.indexOf(set) * getInterval(), scrollMax);
	}
	
	public int getScrollPos()
	{
		return scrollPos;
	}
	
	public void setScrollPos(int scrollPos)
	{
		this.scrollPos = scrollPos;
		invalidate();
		updateGraphics();
	}
	
	public int getScrollMax()
	{
		return scrollMax;
	}
	
	public int getPadding()
	{
		return (int) (GraphicsUtils.getCardWidth() * 0.1);
	}
	
	public int getInterval()
	{
		return GraphicsUtils.getCardWidth() + scale(2) + getPadding();
	}
	
	public int getVisualSetCount()
	{
		return sets.size() + (createSet != null ? 1 : 0);
	}
	
	public boolean isScrollEnabled()
	{
		return scrollEnabled;
	}
	
	public boolean checkScrollRequired()
	{
		scrollMax = Math.max((getVisualSetCount() * getInterval()) - getWidth() - getPadding(), 0);
		scrollPos = Math.min(scrollPos, scrollMax);
		
		boolean wasScrollEnabled = scrollEnabled;
		scrollEnabled = scrollMax > 0;
		
		if (!wasScrollEnabled && scrollEnabled)
		{
			addMouseWheelListener(mouseWheelListener);
		}
		else if (wasScrollEnabled && !scrollEnabled)
		{
			removeMouseWheelListener(mouseWheelListener);
		}
		
		return scrollEnabled;
	}
	
	@Override
	public void doPaint(Graphics g)
	{
		super.doPaint(g);
		if (MDClient.getInstance().isDebugEnabled())
		{
			g.setColor(Color.MAGENTA);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
	
	@Override
	public void paintChildren(Graphics gr)
	{
		super.paintChildren(gr);
		if (scrollEnabled)
		{
			Graphics2D g = (Graphics2D) gr;
			int gradientWidth = scale(12);
			Color opaque = player.getUI().getInnerColor();
			Color transparent = new Color(opaque.getRed(), opaque.getGreen(), opaque.getBlue(), 0);
			if (scrollPos != scrollMax)
			{
				Color[] rightGradient = new Color[] {transparent, opaque};
				LinearGradientPaint paint = new LinearGradientPaint(getWidth() - gradientWidth, 0, getWidth(), 0,
						new float[] {0, 1}, rightGradient);
				g.setPaint(paint);
				g.fillRect(getWidth() - gradientWidth, 0, gradientWidth, getHeight());
			}
			if (scrollPos != 0)
			{
				Color[] leftGradient = new Color[] {opaque, transparent};
				LinearGradientPaint paint = new LinearGradientPaint(0, 0, gradientWidth, 0,
						new float[] {0, 1}, leftGradient);
				g.setPaint(paint);
				g.fillRect(0, 0, gradientWidth, getHeight());
			}
		}
	}
	
	public class PropertySetsLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			int offset = 0;
			if (scrollMax > 0)
			{
				offset = -scrollPos;
			}
			
			for (int i = 0 ; i < sets.size() ; i++)
			{
				sets.get(i).setLocation(offset + getInterval() * i, 0);
				sets.get(i).update();
			}
			if (createSet != null)
			{
				createSet.setLocation(offset + getInterval() * sets.size(), 0);
			}
		}
		
		@Override
		public void invalidateLayout(Container c)
		{
			layoutContainer(c);
		}
		
		@Override
		public void addLayoutComponent(Component component, Object arg1)
		{
			layoutContainer(MDPlayerPropertySets.this);
		}
	}
}
