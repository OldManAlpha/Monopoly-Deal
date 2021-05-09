package oldmana.md.client.gui.component.large;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDTopbar extends MDComponent
{
	private String text;
	
	public MDTopbar()
	{
		super();
		setLocation(0, 0);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.DARK_GRAY);
		Font font = GraphicsUtils.getBoldMDFont(scale(26));
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		TextPainter tp = new TextPainter(text, font, new Rectangle(0, 2, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
	}
}
