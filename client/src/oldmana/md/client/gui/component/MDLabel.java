package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDLabel extends MDComponent
{
	private String text;
	
	public MDLabel(String text)
	{
		this.text = text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setFont(GraphicsUtils.getBoldMDFont(getHeight() - scale(8)));
		g.setColor(Color.DARK_GRAY);
		TextPainter tp = new TextPainter(text, g.getFont(), new Rectangle(0, scale(2), getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
	}
}
