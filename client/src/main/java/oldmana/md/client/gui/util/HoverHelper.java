package oldmana.md.client.gui.util;

import oldmana.md.client.MDClient;
import oldmana.md.client.Scheduler;

import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverHelper
{
	private boolean hovered;
	private double highlight;
	
	public HoverHelper(JComponent component, int maxHighlightTimeMs)
	{
		component.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				hovered = true;
				component.repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				hovered = false;
				component.repaint();
			}
		});
		
		MDClient.getInstance().getScheduler().scheduleFrameboundTask(task ->
		{
			if (!component.isDisplayable())
			{
				task.cancel();
				return;
			}
			if (hovered && highlight < 1 && component.isEnabled())
			{
				highlight = Math.min(highlight + (Scheduler.getFrameDelay() / maxHighlightTimeMs), 1);
				component.repaint();
			}
			else if ((!hovered && highlight > 0) || !component.isEnabled())
			{
				highlight = Math.max(highlight - (Scheduler.getFrameDelay() / maxHighlightTimeMs), 0);
				component.repaint();
			}
		});
	}
	
	public boolean isHovered()
	{
		return hovered;
	}
	
	public double getHighlight()
	{
		return highlight;
	}
}
