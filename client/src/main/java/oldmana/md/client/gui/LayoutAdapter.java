package oldmana.md.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class LayoutAdapter implements LayoutManager2
{
	@Override
	public void addLayoutComponent(String name, Component comp) {}
	
	@Override
	public void layoutContainer(Container parent) {}
	
	@Override
	public Dimension minimumLayoutSize(Container parent) {return null;}
	
	@Override
	public Dimension preferredLayoutSize(Container parent) {return null;}
	
	@Override
	public void removeLayoutComponent(Component comp) {}
	
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {}
	
	@Override
	public float getLayoutAlignmentX(Container target) {return 0;}
	
	@Override
	public float getLayoutAlignmentY(Container target) {return 0;}
	
	@Override
	public void invalidateLayout(Container target) {}
	
	@Override
	public Dimension maximumLayoutSize(Container target) {return null;}
}
