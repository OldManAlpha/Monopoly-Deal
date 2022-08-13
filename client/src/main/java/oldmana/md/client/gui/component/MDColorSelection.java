package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import oldmana.md.client.card.CardProperty.PropertyColor;

public class MDColorSelection extends MDComponent
{
	private PropertyColor color;
	private boolean selected;
	
	public MDColorSelection(PropertyColor color, boolean selected)
	{
		this.color = color;
		this.selected = selected;
		setSize(scale(80), scale(80));
	}
	
	public PropertyColor getColor()
	{
		return color;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setColor(color.getColor());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(selected ? Color.BLUE : Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		if (selected)
		{
			g.drawRect(1, 1, getWidth() - 1 - 2, getHeight() - 1 - 2);
		}
	}
}
