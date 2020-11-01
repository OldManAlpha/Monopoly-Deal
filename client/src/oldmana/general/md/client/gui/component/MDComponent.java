package oldmana.general.md.client.gui.component;

import javax.swing.JComponent;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.gui.util.GraphicsUtils;

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
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
}
