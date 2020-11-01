package oldmana.md.client.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardAction;
import oldmana.md.client.card.CardMoney;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.CardSpecial;
import oldmana.md.client.card.CardProperty.PropertyColor;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class GraphicsUtils
{
	public static double SCALE = 1;
	
	public static BufferedImage createImage(int width, int height)
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}
	
	public static Font getBoldMDFont(int size)
	{
		return new Font("ITCKabelStd-Bold", Font.PLAIN, size);
	}
	
	public static Font getBoldMDFont(int style, int size)
	{
		return new Font("ITCKabelStd-Bold", style, size);
	}
	
	public static Font getThinMDFont(int size)
	{
		return new Font("ITCKabelStd-Book", Font.PLAIN, size);
	}
	
	public static Font getThinMDFont(int style, int size)
	{
		return new Font("ITCKabelStd-Book", style, size);
	}
	
	public static int scale(int size)
	{
		return (int) (SCALE * size);
	}
	
	public static int getCardWidth(double scale)
	{
		return (int) (SCALE * scale * 60);
	}
	
	public static int getCardWidth()
	{
		return getCardWidth(1);
	}
	
	public static int getCardHeight(double scale)
	{
		return (int) (SCALE * scale * 90);
	}
	
	public static int getCardHeight()
	{
		return getCardHeight(1);
	}
	
	public static void drawDebug(Graphics2D g, String text, int fontSize, int width, int height)
	{
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(getBoldMDFont(fontSize));
		FontMetrics m = g.getFontMetrics();
		int strWidth = m.stringWidth(text);
		int strHeight = m.getHeight();
		g.fillRect((width / 2) - (strWidth / 2), (height / 2) - (strHeight / 2), strWidth, strHeight);
		g.setColor(Color.BLACK);
		g.drawString(text, (width / 2) - (strWidth / 2), (height / 2) + (strHeight / 2));
	}
	
	public static void redispatchMouseEvent(JComponent glassPane, Container container, MouseEvent e)
	{
		Point glassPanePoint = e.getPoint();
		Point containerPoint = SwingUtilities.convertPoint(
		              glassPane,
		              glassPanePoint,
		              container);
		
		if (containerPoint.y < 0)
		{ //we're not in the content pane
				//Could have special code to handle mouse events over
				//the menu bar or non-system window decorations, such as
				//the ones provided by the Java look and feel.
		}
		else
		{
			//The mouse event is probably over the content pane.
			//Find out exactly which component it's over.
			Component component =
			SwingUtilities.getDeepestComponentAt(
			              container,
			              containerPoint.x,
			              containerPoint.y);
			System.out.println(component);
			
			if (component != null)
			{
				//Forward events over the check box.
				Point componentPoint = SwingUtilities.convertPoint(
				                  glassPane,
				                  glassPanePoint,
				                  component);
				component.dispatchEvent(new MouseEvent(component,
				                           e.getID(),
				                           e.getWhen(),
				                           e.getModifiers(),
				                           componentPoint.x,
				                           componentPoint.y,
				                           e.getClickCount(),
				                           e.isPopupTrigger()));
			}
		}
	}
	
	public static List<String> splitString(String str, FontMetrics metrics, int lineWidth)
	{
		List<String> lines = new ArrayList<String>();
		String line = "";
		for (char c : str.toCharArray())
		{
			if (metrics.stringWidth(line + c) > lineWidth)
			{
				lines.add(line);
				line = "";
			}
			line += c;
		}
		lines.add(line);
		return lines;
	}
	
	/*
	public static void drawCard(Card card, Graphics gr, int width, int height)
	{
		boolean money = card instanceof CardMoney;
		boolean property = card instanceof CardProperty;
		boolean action = card instanceof CardAction;
		boolean special = card instanceof CardSpecial;
		
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw White
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getWidth() / 6, getWidth() / 6);
		// Drawing for known cards
		if (card != null)
		{
			// Draw Value Color
			if (!property)
			{
				g.setColor(card.getValueColor());
				//g.fillRoundRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1, getWidth() / 6, getWidth() / 6);
				g.fillRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1);
			}
			// Draw Card Outline
			g.setColor(Color.BLACK);
			g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getWidth() / 6, getWidth() / 6);
			// Draw Property Color
			if (property)
			{
				CardProperty prop = (CardProperty) card;
				List<PropertyColor> colors = prop.getColors();
				double interval = (double) (getWidth() - scale(12)) / (double) (colors.size());
				for (int i = 0 ; i < colors.size() ; i++)
				{
					g.setColor(colors.get(i).getColor());
					g.fillRect(scale(6) + (int) Math.ceil((interval * i)), scale(6), (int) Math.ceil(interval), scale(17));
				}
				g.setColor(Color.BLACK);
				g.drawRect(scale(6), scale(6), getWidth() - scale(12) - 1, scale(17) - 1);
				//g.setColor(prop.getColor().getColor());
				//g.fillRect(scale(6), scale(6), getWidth() - scale(12), scale(17));
				// Old rounded corners color design
				//g.fillRoundRect(scale(3) + 1, scale(3), getWidth() - scale(6) - 2, scale(15), getWidth() / 6, getWidth() / 6);
				//g.fillRect(scale(3), scale(10), getWidth() - scale(6) - 1, scale(8));
			}
			
			// Draw Card Value And Value Border
			if (card.getValue() > 0)
			{
				if (property)
				{
					g.setColor(Color.WHITE);
					g.fillOval(scale(4), scale(4), scale(12), scale(12));
				}
				//else
				{
					if (!property)
					{
						Color gray = Color.GRAY;
						Color valueColor = card.getValueColor();
						Color color = new Color((gray.getRed() + valueColor.getRed()) / 2, (gray.getGreen() + valueColor.getGreen()) / 2, 
								(gray.getBlue() + valueColor.getBlue()) / 2);
						g.setColor(color);
						for (int i = 0 ; i < scale(3) ; i++)
						{
							g.drawRect(scale(8) + i, scale(8) + i, getWidth() - scale(16) - (i * 2) - 1, getHeight() - (i * 2) - scale(16) - 1);
						}
					}
					if (!property)
					{
						g.setColor(card.getValueColor());
						g.fillOval(scale(4), scale(4), scale(12), scale(12));
						g.fillOval(scale(56 - 12) - 1, scale(86 - 12) - 1, scale(12), scale(12));
					}
					if (action || special)
					{
						g.setColor(new Color(200, 0, 0));
					}
					else
					{
						g.setColor(Color.BLACK);
					}
					g.drawOval(scale(4), scale(4), scale(12), scale(12));
					//g.setColor(Color.BLACK);
					//g.drawOval(scale(5) - 1, scale(5) - 1, scale(12) + 2, scale(12) + 2);
					if (!property)
					{
						g.drawOval(scale(56 - 12) - 1, scale(86 - 12) - 1, scale(12), scale(12));
					}
				}
				g.setColor(Color.BLACK);
				Font font = new Font(getFont().getFontName(), Font.BOLD, card.getValue() < 10 ? scale(6) : scale(6));
				g.setFont(font);
				TextPainter tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(3), scale(5), scale(card.getValue() < 10 ? 15 : 14), scale(10)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
				if (!property)
				{
					tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(56 - 12 - 1), scale(86 - 12) + 1, scale(14), scale(10)));
					tp.setHorizontalAlignment(Alignment.CENTER);
					tp.setVerticalAlignment(Alignment.CENTER);
					tp.paint(g);
				}
			}
			// Draw Inner Outline
			g.setColor(Color.BLACK);
			//g.drawRoundRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1, getWidth() / 6, getWidth() / 6);
			g.drawRect(scale(3), scale(3), getWidth() - scale(6) - 1, getHeight() - scale(6) - 1);
			
			// Draw Card Name/Info
			if (money)
			{
				g.setColor(new Color(30, 30, 30));
				for (int i = 0 ; i < scale(1) ; i++)
				{
					g.drawOval(scale(11) + i, scale(26) + i, scale(38) - (i * 2) - 1, scale(38) - (i * 2) - 1);
				}
				Font font = new Font(getFont().getFontName(), Font.BOLD, scale(16));
				g.setFont(font);
				TextPainter tp = new TextPainter(card.getValue() + "M", font, new Rectangle(scale(0), scale(30), getWidth(), scale(30)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			else
			{
				Font font = new Font(getFont().getFontName(), Font.BOLD, scale(6));
				g.setFont(font);
				TextPainter tp = new TextPainter(property ? card.getName() : "Action Card", font, new Rectangle(scale(4), scale(property ? 24 : 16), getWidth() - scale(8), scale(20)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.TOP);
				tp.paint(g);
				
				if (!property)
				{
					g.setColor(Color.WHITE);
					g.fillOval(scale(12.5), scale(27.5), scale(35), scale(35));
					g.setColor(Color.BLACK);
					for (int i = 0 ; i < scale(1.5) ; i++)
					{
						g.drawOval(scale(11) + i, scale(26) + i, scale(38) - (i * 2) - 1, scale(38) - (i * 2) - 1);
					}
					font = new Font(getFont().getFontName(), Font.BOLD, scale(8));
					g.setFont(font);
					//tp = new TextPainter(Arrays.asList(new String[] {"JUST", "SAY NO!"}), font, new Rectangle(scale(14), scale(28), scale(32), scale(30)), false, true);
					tp = new TextPainter(card.getName(), font, new Rectangle(scale(14) - 1, scale(28) - 1, scale(32) + 1, scale(30) + 1));
					tp.setHorizontalAlignment(Alignment.CENTER);
					tp.setVerticalAlignment(Alignment.CENTER);
					tp.paint(g);
				}
			}
		}
		else // Unknown card
		{
			// Draw Card Outline
			g.setColor(Color.BLACK);
			g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getWidth() / 6, getWidth() / 6);
			g.setColor(new Color(239, 15, 20));
			g.fillRoundRect(scale(4), scale(4), getWidth() - scale(8) - 1, getHeight() - scale(8) - 1, getWidth() / 6, getWidth() / 6);
			// Draw Inner Outline
			g.setColor(Color.BLACK);
			g.drawRoundRect(scale(4), scale(4), getWidth() - scale(8) - 1, getHeight() - scale(8) - 1, getWidth() / 6, getWidth() / 6);
		}
	}
	*/
}
