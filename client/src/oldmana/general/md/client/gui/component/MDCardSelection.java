package oldmana.general.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.general.md.client.card.Card;

public class MDCardSelection extends MDCard
{
	private boolean disabled;
	private boolean selected = false;
	
	private CardSelectListener listener;
	
	public MDCardSelection(Card card)
	{
		super(card, 2);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				if (!disabled)
				{
					if (listener != null && !selected)
					{
						if (!listener.preSelect())
						{
							return;
						}
					}
					selected = !selected;
					repaint();
					if (listener != null)
					{
						listener.postSelectToggle();
					}
				}
			}
		});
	}
	
	public MDCardSelection(Card card, boolean selected)
	{
		this(card);
		this.selected = selected;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setListener(CardSelectListener listener)
	{
		this.listener = listener;
	}
	
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		//g.drawImage(card.getGraphics(4), 0, 0, getWidth(), getHeight(), null);
		if (selected)
		{
			Polygon check = new Polygon();
			check.addPoint(58, 127);
			check.addPoint(102, 71);
			check.addPoint(92, 66);
			check.addPoint(59, 112);
			check.addPoint(25, 90);
			check.addPoint(18, 99);
			g.setColor(Color.GREEN);
			g.fillPolygon(check);
			g.setColor(Color.BLACK);
			g.drawPolygon(check);
		}
	}
	
	public static interface CardSelectListener
	{
		public boolean preSelect();
		
		public void postSelectToggle();
	}
}
