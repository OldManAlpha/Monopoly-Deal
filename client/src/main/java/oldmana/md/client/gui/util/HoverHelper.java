package oldmana.md.client.gui.util;

import oldmana.md.client.MDClient;
import oldmana.md.client.Scheduler;
import oldmana.md.client.gui.component.MDComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverHelper
{
	private boolean hovered;
	private double highlight;
	
	public HoverHelper(MDComponent component, int maxHighlightTimeMs)
	{
		component.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				hovered = true;
				component.updateGraphics();
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				hovered = false;
				component.updateGraphics();
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
				component.updateGraphics();
			}
			else if ((!hovered && highlight > 0)/* || !component.isEnabled()*/)
			{
				highlight = Math.max(highlight - (Scheduler.getFrameDelay() / maxHighlightTimeMs), 0);
				component.updateGraphics();
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
