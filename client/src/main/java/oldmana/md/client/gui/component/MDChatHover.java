package oldmana.md.client.gui.component;

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;

public class MDChatHover extends MDComponent
{
	private List<String> text;
	private List<String> splitText;
	
	public MDChatHover(List<String> text)
	{
		setText(text);
	}
	
	public List<String> getText()
	{
		return text;
	}
	
	public void setText(List<String> text)
	{
		this.text = text;
		
		splitText = GraphicsUtils.splitStrings(text, getFont(), scale(500), true);
		
		FontMetrics metrics = getFontMetrics(getFont());
		int largestWidth = scale(20);
		for (String line : splitText)
		{
			largestWidth = Math.max(largestWidth, metrics.stringWidth(line));
		}
		int height = getFont().getSize() * splitText.size();
		setSize(largestWidth + (getBorderSize() * 2), height + (getBorderSize() * 2));
	}
	
	private int getBorderSize()
	{
		return scale(5);
	}
	
	@Override
	public Font getFont()
	{
		return GraphicsUtils.getThinMDFont(scale(24));
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int visualBorder = scale(2);
		LinearGradientPaint paint = new LinearGradientPaint(0, 0, getWidth(), getHeight(),
				new float[] {0, 1}, new Color[] {new Color(120, 140, 150), new Color(100, 120, 130)});
		g.setPaint(paint);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), scale(10), scale(10));
		paint = new LinearGradientPaint(0, 0, getWidth(), getHeight(),
				new float[] {0, 1}, new Color[] {new Color(220, 220, 220), new Color(190, 190, 190)});
		g.setPaint(paint);
		g.fillRoundRect(visualBorder, visualBorder, getWidth() - (visualBorder * 2), getHeight() - (visualBorder * 2), scale(10) - visualBorder, scale(10) - visualBorder);
		g.setColor(Color.DARK_GRAY);
		int border = getBorderSize();
		TextPainter tp = new TextPainter(splitText, getFont(), new Rectangle(border, border, getWidth() - (border * 2),
				getHeight() - (border * 2)), false, false);
		tp.paint(g);
	}
}
