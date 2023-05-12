package oldmana.md.client.gui.component;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

import javax.swing.JComponent;

import oldmana.md.client.MDClient;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDComponent extends JComponent
{
	public int scale(int size)
	{
		return GraphicsUtils.scale(size);
	}
	
	public int scale(double size)
	{
		return GraphicsUtils.scale(size);
	}
	
	public double getScale()
	{
		return GraphicsUtils.SCALE;
	}
	
	/**Convenience method to round down doubles.
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height)
	{
		setSize((int) width, (int) height);
	}
	
	public Point getCenter()
	{
		return new Point(getX() + (getWidth() / 2), getY() + (getHeight() / 2));
	}
	
	public void setLocation(double x, double y)
	{
		setLocation((int) x, (int) y);
	}
	
	public void setLocationCentered(int x, int y)
	{
		setLocation(x - (getWidth() / 2), y - (getHeight() / 2));
	}
	
	/**Convenience method to round down doubles.
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocationCentered(double x, double y)
	{
		setLocationCentered((int) x, (int) y);
	}
	
	public void setLocationCenterX(int xCenter, int y)
	{
		setLocation(xCenter - (getWidth() / 2), y);
	}
	
	public void setLocationCenterY(int x, int yCenter)
	{
		setLocation(x, yCenter - (getHeight() / 2));
	}
	
	public int getMaxX()
	{
		return getX() + getWidth();
	}
	
	public int getMaxY()
	{
		return getY() + getHeight();
	}
	
	public MouseAdapter addClickListener(Consumer<MouseEvent> func)
	{
		MouseAdapter listener = new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				func.accept(event);
			}
		};
		addMouseListener(listener);
		return listener;
	}
	
	public MouseAdapter addClickListener(Runnable func)
	{
		return addClickListener(event -> func.run());
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
