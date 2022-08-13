package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDPlayerPropertySets extends MDComponent
{
	private Player player; 
	
	private List<MDPropertySet> sets = new ArrayList<MDPropertySet>();
	
	public MDPlayerPropertySets(Player player)
	{
		this.player = player;
	}
	
	public void addPropertySet(MDPropertySet set)
	{
		add(set, 0);
		sets.add(set);
		update();
	}
	
	public void removePropertySet(MDPropertySet set)
	{
		set.onMouseExited();
		remove(set);
		sets.remove(set);
		update();
	}
	
	public int getNextPropertySetLocX()
	{
		return (int) (sets.size() * (GraphicsUtils.getCardWidth() * 1.1 + scale(2)));
	}
	
	public void update()
	{
		for (int i = 0 ; i < sets.size() ; i++)
		{
			sets.get(i).setLocation((int) (i * (GraphicsUtils.getCardWidth() * 1.1 + scale(2))), 0);
			sets.get(i).update();
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (MDClient.getInstance().isDebugEnabled())
		{
			g.setColor(Color.MAGENTA);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
	
	public class PropertySetsLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			
		}
		
		@Override
		public void addLayoutComponent(Component component, Object arg1)
		{
			layoutContainer(MDPlayerPropertySets.this);
		}
	}
}
