package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;

public class MDLabel extends MDComponent
{
	private String text;
	private int size;
	private double padding = 20;
	
	public MDLabel(String text)
	{
		this.text = text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		sizeLabel();
	}
	
	public void setPadding(double padding)
	{
		this.padding = padding;
		sizeLabel();
	}
	
	public void sizeLabel()
	{
		if (size > 0)
		{
			setSize(getAppropriateWidth(size), size);
		}
	}
	
	public int getAppropriateWidth(int height)
	{
		FontMetrics m = getFontMetrics(GraphicsUtils.getBoldMDFont(height - scale(8)));
		return m.stringWidth(text) + scale(padding);
	}
	
	public void setSize(int size)
	{
		this.size = size;
		sizeLabel();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		LinearGradientPaint grayPaint = new LinearGradientPaint(0, 0, 0, getHeight(),
				new float[] {0, 0.45F, 0.451F, 1}, new Color[] {new Color(225, 225, 225), new Color(215, 215, 215), new Color(200, 200, 200),
				new Color(190, 190, 190)});
		
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		
		//g.setColor(Color.LIGHT_GRAY);
		g.setPaint(grayPaint);
		g.fillRoundRect(scale(2), scale(2), getWidth() - 1 - scale(4), getHeight() - 1 - scale(4), scale(8), scale(8));
		
		
		g.setFont(GraphicsUtils.getBoldMDFont(getHeight() - scale(8)));
		g.setColor(Color.DARK_GRAY);
		TextPainter tp = new TextPainter(text, g.getFont(), new Rectangle(0, scale(2), getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
}
