package oldmana.md.client.gui.action;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import oldmana.md.client.gui.component.MDComponent;

public class ActionScreen extends MDComponent
{
	public ActionScreen()
	{
		super();
		addMouseListener(new MouseAdapter() {});
		addMouseMotionListener(new MouseMotionAdapter() {});
	}
	
	public void initialize() {}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
