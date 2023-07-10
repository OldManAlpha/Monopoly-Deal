package oldmana.md.client.gui.component;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JComponent;

import oldmana.md.client.MDClient;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDComponent extends JComponent
{
	private Image buffer;
	private boolean updateRequested = true;
	
	@Override
	public void paintComponent(Graphics g)
	{
		if (buffer == null)
		{
			createBuffer();
			updateRequested = true;
		}
		/*
		if (buffer instanceof VolatileImage)
		{
			if (((VolatileImage) buffer).validate(getClient().getWindow().getGraphicsConfiguration()) != VolatileImage.IMAGE_OK)
			{
				createBuffer();
				updateRequested = true;
			}
		}
		*/
		if (updateRequested)
		{
			Graphics2D bufferGraphics = (Graphics2D) buffer.getGraphics();
			bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0F));
			bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
			bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			doPaint(bufferGraphics);
			bufferGraphics.dispose();
			updateRequested = false;
		}
		g.drawImage(buffer, 0, 0, null);
	}
	
	public void doPaint(Graphics g) {}
	
	public Image getBuffer()
	{
		return buffer;
	}
	
	protected void createBuffer()
	{
		buffer = GraphicsUtils.createImage(Math.max(1, getWidth()), Math.max(1, getHeight()));
	}
	
	/**
	 * Requests the component to be repainted and re-renders the component on the next repaint.
	 */
	public void updateGraphics()
	{
		updateRequested = true;
		repaint();
	}
	
	@Override
	public void setSize(int width, int height)
	{
		int prevWidth = getWidth();
		int prevHeight = getHeight();
		super.setSize(width, height);
		if (prevWidth != width || prevHeight != height)
		{
			buffer = null;
			updateRequested = true;
		}
	}
	
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
	
	public int getCenterX()
	{
		return getX() + (getWidth() / 2);
	}
	
	public int getCenterY()
	{
		return getY() + (getHeight() / 2);
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
				if (event.getX() >= 0 && event.getX() < getWidth() && event.getY() >= 0 && event.getY() < getHeight())
				{
					func.accept(event);
				}
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
