package oldmana.md.client.gui.component;

import java.awt.Point;

import javax.swing.JComponent;

import oldmana.md.client.MDClient;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDComponent extends JComponent
{
	public int scale(int size)
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
	
	public int getMaxX()
	{
		return getX() + getWidth();
	}
	
	public int getMaxY()
	{
		return getY() + getHeight();
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
