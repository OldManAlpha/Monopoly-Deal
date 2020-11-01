package oldmana.general.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.MDScheduler.MDTask;

public class MDSelection extends MDComponent
{
	public static Color DEFAULT_COLOR = new Color(240, 240, 0);
	
	private int lineSize = 20;
	private int pos;
	
	private Color color;
	
	public MDSelection()
	{
		this(DEFAULT_COLOR);
	}
	
	public MDSelection(Color color)
	{
		super();
		this.color = color;
		MDClient.getInstance().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				if (SwingUtilities.getUnwrappedParent(MDSelection.this) == null)
				{
					cancel();
				}
				else
				{
					pos = (pos + 1) % lineSize;
					repaint();
				}
			}
		});
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(color);
		int lineSize = scale(this.lineSize);
		int pos = scale(this.pos);
		for (int i = -1 ; i - 1 < getWidth() / lineSize ; i++)
		{
			g.fillRect((i * lineSize) + pos, 0, lineSize / 2, scale(4));
			g.fillRect(getWidth() - ((i * lineSize) + pos), getHeight() - 1 - scale(4), lineSize / 2, scale(4));
		}
		for (int i = -1 ; i - 1 < getHeight() / lineSize ; i++)
		{
			g.fillRect(0, getHeight() - ((i * lineSize) + pos), scale(4), lineSize / 2);
			g.fillRect(getWidth() - 1 - scale(4), (i * lineSize) + pos, scale(4), lineSize / 2);
		}
	}
}
