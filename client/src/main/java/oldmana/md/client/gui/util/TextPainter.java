package oldmana.md.client.gui.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class TextPainter
{
	private List<String> text;
	private Font font;
	
	private Rectangle bounds;
	
	private boolean autoReturn;
	private boolean attachWords;
	
	private Alignment horizontal = Alignment.LEFT;
	private Alignment vertical = Alignment.TOP;
	
	private Outline outline;
	
	public TextPainter(List<String> text, Font font, Rectangle bounds, boolean autoReturn, boolean attachWords)
	{
		this.text = text;
		this.font = font;
		
		this.bounds = bounds;
		
		this.autoReturn = autoReturn;
		this.attachWords = attachWords;
	}
	
	public TextPainter(String text, Font font, Rectangle bounds, boolean autoReturn, boolean attachWords)
	{
		List<String> list = new ArrayList<String>(1);
		list.add(text);
		this.text = list;
		this.font = font;
		
		this.bounds = bounds;
		
		this.autoReturn = autoReturn;
		this.attachWords = attachWords;
	}
	
	public TextPainter(List<String> text, Font font, Rectangle bounds, boolean autoReturn, boolean attachWords, Outline outline)
	{
		this.text = text;
		this.font = font;
		
		this.bounds = bounds;
		
		this.autoReturn = autoReturn;
		this.attachWords = attachWords;
		
		this.outline = outline;
	}
	
	public TextPainter(String text, Font font, Rectangle bounds, boolean autoReturn, boolean attachWords, Outline outline)
	{
		List<String> list = new ArrayList<String>(1);
		list.add(text);
		this.text = list;
		this.font = font;
		
		this.bounds = bounds;
		
		this.autoReturn = autoReturn;
		this.attachWords = attachWords;
		
		this.outline = outline;
	}
	
	public TextPainter(String text, Font font, Rectangle bounds)
	{
		this(text, font, bounds, true, true);
	}
	
	public TextPainter(String text, Font font, Rectangle bounds, Outline outline)
	{
		this(text, font, bounds, true, true, outline);
	}
	
	public void setHorizontalAlignment(Alignment a)
	{
		if (a == Alignment.TOP || a == Alignment.BOTTOM)
		{
			throw new IllegalArgumentException("Invalid alignment for the given axis.");
		}
		horizontal = a;
	}
	
	public void setVerticalAlignment(Alignment a)
	{
		if (a == Alignment.LEFT || a == Alignment.RIGHT)
		{
			throw new IllegalArgumentException("Invalid alignment for the given axis.");
		}
		vertical = a;
	}
	
	public void setOutline(Outline outline)
	{
		this.outline = outline;
	}
	
	public void paint(Graphics2D whole)
	{
		Graphics2D g = (Graphics2D) whole.create((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(), 
				(int) bounds.getHeight());
		whole.translate(bounds.getMinX(), bounds.getMinY());
		g = whole;
		g.setFont(font);
		FontMetrics m = g.getFontMetrics();
		List<String> lines = new ArrayList<String>();
		if (autoReturn)
		{
			for (String str : text)
			{
				lines.addAll(GraphicsUtils.splitString(str, font, (int) bounds.getWidth(), attachWords));
				/*
				char[] chars = str.toCharArray();
				String line = "";
				for (char c : chars)
				{
					line += c;
					if (m.stringWidth(line) > bounds.getWidth())
					{
						if (attachWords)
						{
							if (c == ' ')
							{
								lines.add(line.substring(0, line.length() - 1));
								line = "" + c;
							}
							else
							{
								char[] charz = line.toCharArray();
								boolean addedLine = false;
								for (int i = charz.length - 1 ; i > 0 ; i--)
								{
									if (charz[i] == ' ')
									{
										lines.add(line.substring(0, i));
										line = line.substring(i + 1);
										
										addedLine = true;
										break;
									}
								}
								if (!addedLine)
								{
									lines.add(line);
									line = "";
								}
							}
						}
						else
						{
							lines.add(line.substring(0, line.length() - 1));
							line = "" + c;
						}
					}
				}
				if (!line.equals(""))
				{
					lines.add(line);
				}
				*/
			}
			
			int verticalAddition = 0;
			if (vertical == Alignment.CENTER)
			{
				verticalAddition = ((int) bounds.getHeight() - (lines.size() * font.getSize())) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - (lines.size() * font.getSize());
			}
			
			for (int i = 0 ; i < lines.size() ; i++)
			{
				int horizontalAddition = 0;
				if (horizontal == Alignment.CENTER)
				{
					horizontalAddition = ((int) bounds.getWidth() - m.stringWidth(lines.get(i))) / 2;
				}
				else if (horizontal == Alignment.RIGHT)
				{
					horizontalAddition = (int) bounds.getWidth() - m.stringWidth(lines.get(i));
				}
				if (outline != null)
				{
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					//AffineTransform transform = g2d.getTransform();
					g2d.translate(horizontalAddition, verticalAddition + (i * font.getSize()) + getAscent());
					//g2d.transform(transform);
					g2d.setColor(outline.color);
					FontRenderContext frc = g2d.getFontRenderContext();
					TextLayout tl = new TextLayout(lines.get(i), font, frc);
					Shape shape = tl.getOutline(null);
					g2d.setStroke(new BasicStroke((float) outline.size));
					g2d.draw(shape);
					g2d.setColor(g.getColor());
					g2d.fill(shape);
				}
				else
				{
					g.drawString(lines.get(i), horizontalAddition, verticalAddition + (i * font.getSize()) + getAscent());
				}
			}
		}
		else
		{
			/*
			int verticalAddition = 0;
			if (vertical == Alignment.CENTER)
			{
				verticalAddition = ((int) bounds.getHeight() - font.getSize()) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - font.getSize();
			}
			int horizontalAddition = 0;
			if (horizontal == Alignment.CENTER)
			{
				horizontalAddition = ((int) bounds.getWidth() - m.stringWidth(text.get(0))) / 2;
			}
			else if (horizontal == Alignment.RIGHT)
			{
				horizontalAddition = (int) bounds.getWidth() - m.stringWidth(text.get(0));
			}
			g.drawString(text.get(0), horizontalAddition, verticalAddition + m.getAscent());
			*/
			int verticalAddition = 0;
			if (vertical == Alignment.CENTER)
			{
				verticalAddition = ((int) bounds.getHeight() - (text.size() * font.getSize())) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - (text.size() * font.getSize());
			}
			
			for (int i = 0 ; i < text.size() ; i++)
			{
				int horizontalAddition = 0;
				if (horizontal == Alignment.CENTER)
				{
					horizontalAddition = ((int) bounds.getWidth() - m.stringWidth(text.get(i))) / 2;
				}
				else if (horizontal == Alignment.RIGHT)
				{
					horizontalAddition = (int) bounds.getWidth() - m.stringWidth(text.get(i));
				}
				if (outline != null)
				{
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					//AffineTransform transform = g2d.getTransform();
					g2d.translate(horizontalAddition, verticalAddition + (i * font.getSize()) + getAscent());
					//g2d.transform(transform);
					g2d.setColor(outline.color);
					FontRenderContext frc = g2d.getFontRenderContext();
					TextLayout tl = new TextLayout(text.get(i), font, frc);
					Shape shape = tl.getOutline(null);
					g2d.setStroke(new BasicStroke((float) outline.size));
					g2d.draw(shape);
					g2d.setColor(g.getColor());
					g2d.fill(shape);
				}
				else
				{
					g.drawString(text.get(i), horizontalAddition, verticalAddition + (i * font.getSize()) + getAscent());
				}
			}
		}
		g.translate(-bounds.getMinX(), -bounds.getMinY());
	}
	
	public int getAscent()
	{
		return (int) (font.getSize() * 0.75);
	}
	
	public static class TextMeasurement
	{
		private FontMetrics m;
		
		public TextMeasurement(FontMetrics metrics)
		{
			m = metrics;
		}
		
		public int getWidth(String text)
		{
			return m.stringWidth(text);
		}
		
		public int getHeight()
		{
			return m.getFont().getSize();
		}
		
		public String getHorizontalVisibleText(String text, int availableWidth, int pos)
		{
			StringBuilder sb = new StringBuilder(text);
			if (getWidth(text.substring(0, pos)) > availableWidth)
			{
				sb = new StringBuilder(sb.substring(0, pos));
				while (getWidth(sb.toString()) > availableWidth)
				{
					sb.deleteCharAt(0);
				}
			}
			return sb.toString();
		}
	}
	
	public static class Outline
	{
		public Color color;
		public double size;
		
		private Outline(Color color, double size)
		{
			this.color = color;
			this.size = size;
		}
		
		public static Outline of(Color color, double size)
		{
			return new Outline(color, size);
		}
	}
	
	public enum Alignment
	{
		LEFT, RIGHT, TOP, BOTTOM, CENTER
	}
	
	public static void paint(Graphics g, String text, Font font, int startX, int startY, int width, int height)
	{
		TextPainter tp = new TextPainter(text, font, new Rectangle(startX, startY, width, height));
		tp.paint((Graphics2D) g);
	}
	
	public static void paint(Graphics g, String text, Font font, int startX, int startY, int width, int height, Alignment horizontal, Alignment vertical)
	{
		TextPainter tp = new TextPainter(text, font, new Rectangle(startX, startY, width, height));
		tp.setHorizontalAlignment(horizontal);
		tp.setVerticalAlignment(vertical);
		tp.paint((Graphics2D) g);
	}
	
	public static void paint(Graphics g, String text, Font font, int startX, int startY, int width, int height, Alignment horizontal, Alignment vertical, Outline outline)
	{
		TextPainter tp = new TextPainter(text, font, new Rectangle(startX, startY, width, height));
		tp.setHorizontalAlignment(horizontal);
		tp.setVerticalAlignment(vertical);
		tp.setOutline(outline);
		tp.paint((Graphics2D) g);
	}
}
