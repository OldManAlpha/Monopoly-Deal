package oldmana.md.client.gui.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
	
	public TextPainter(String text, Font font, Rectangle bounds)
	{
		this(text, font, bounds, true, true);
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
			}
			
			int verticalAddition = 0;
			if (vertical == Alignment.CENTER)
			{
				verticalAddition = ((int) bounds.getHeight() - (lines.size() * m.getHeight())) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - (lines.size() * m.getHeight());
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
				g.drawString(lines.get(i), horizontalAddition, verticalAddition + (i * m.getHeight()) + m.getAscent());
			}
		}
		else
		{
			/*
			int verticalAddition = 0;
			if (vertical == Alignment.CENTER)
			{
				verticalAddition = ((int) bounds.getHeight() - m.getHeight()) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - m.getHeight();
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
				verticalAddition = ((int) bounds.getHeight() - (text.size() * m.getHeight())) / 2;
			}
			else if (vertical == Alignment.BOTTOM)
			{
				verticalAddition = (int) bounds.getHeight() - (text.size() * m.getHeight());
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
				g.drawString(text.get(i), horizontalAddition, verticalAddition + (i * m.getHeight()) + m.getAscent());
			}
		}
		g.translate(-bounds.getMinX(), -bounds.getMinY());
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
			return m.getHeight();
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
	
	public enum Alignment
	{
		LEFT, RIGHT, TOP, BOTTOM, CENTER
	}
}
