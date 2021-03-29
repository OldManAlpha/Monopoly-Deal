package oldmana.md.client.gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDText extends MDComponent
{
	private String text;
	private int fontSize = 12;
	
	public MDText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setFontSize(int size)
	{
		fontSize = size;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		TextPainter tp = new TextPainter(text, GraphicsUtils.getThinMDFont(scale(fontSize)), new Rectangle(0, 0, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.LEFT);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
}
