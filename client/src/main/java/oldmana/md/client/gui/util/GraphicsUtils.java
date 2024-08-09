package oldmana.md.client.gui.util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import oldmana.md.client.MDClient;
import oldmana.md.client.Settings;

public class GraphicsUtils
{
	public static double SCALE = 1;
	
	public static Color BROWN = new Color(134, 70, 27);
	public static Color LIGHT_BLUE = new Color(187, 222, 241);
	public static Color MAGENTA = new Color(189, 47, 131);
	public static Color ORANGE = new Color(227, 139, 3);
	public static Color RED = new Color(215, 16, 37);
	public static Color YELLOW = new Color(249, 239, 4);
	public static Color GREEN = new Color(80, 180, 47);
	public static Color DARK_BLUE = new Color(64, 92, 165);
	public static Color RAILROAD = new Color(17, 17, 14);
	public static Color UTILITY = new Color(206, 229, 183);
	
	private static Canvas canvas = new Canvas();
	
	/**
	 * Required to set scale through this method to avoid precision issues
	 *
	 * @param scale
	 */
	public static void setScale(double scale)
	{
		setScale(scale, true);
	}
	
	public static void setScale(double scale, boolean save)
	{
		SCALE = roundScale(scale);
		if (save)
		{
			Settings s = MDClient.getInstance().getSettings();
			s.put("scale", SCALE);
			s.saveSettings();
		}
	}
	
	public static double roundScale(double scale)
	{
		return ((int) Math.round(scale * 10)) / 10.0;
	}
	
	public static BufferedImage createImage(int width, int height)
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}
	
	public static BufferedImage createCopy(BufferedImage image)
	{
		return new BufferedImage(image.getColorModel(),
				image.copyData(image.getRaster().createCompatibleWritableRaster()), image.isAlphaPremultiplied(), null);
	}
	
	/**
	 * For better quality images that don't cost a ton of CPU time. Still not the best quality.
	 */
	public static BufferedImage scaleImage(BufferedImage img, int targetWidth, int targetHeight, boolean higherQuality)
	{
		// functionality not supported in java 1.4
		int transparency = Transparency.OPAQUE;
		try
		{
			transparency = img.getTransparency();
		}
		catch (Exception e) {}
		int type = (transparency == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		
		BufferedImage ret = img;
		int w, h;
		if (higherQuality)
		{
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		}
		else
		{
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}
		
		do
		{
			if (higherQuality && w > targetWidth)
			{
				w = (int) Math.round(w * 0.5);
				if (w < targetWidth)
				{
					w = targetWidth;
				}
			}
			
			if (higherQuality && h > targetHeight)
			{
				h = (int) Math.round(h * 0.5);
				if (h < targetHeight)
				{
					h = targetHeight;
				}
			}
			
			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();
			
			ret = tmp;
		}
		while (w != targetWidth || h != targetHeight);
		
		return ret;
	}
	
	/**
	 * Scales an image with very high quality, and high cost.
	 */
	public static BufferedImage scaleImageQuality(BufferedImage image, int drawingWidth, int drawingHeight)
	{
		int origWidth = image.getWidth();
		int origHeight = image.getHeight();
		
		double widthScale = drawingWidth / (double) origWidth;
		double heightScale = drawingHeight / (double) origHeight;
		double scale = Math.min(widthScale, heightScale);
		
		int width = (int) (origWidth * scale);
		int height = (int) (origHeight * scale);
		
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		
		ImageProducer prod = new FilteredImageSource(image.getSource(), new AreaAveragingScaleFilter(width, height));
		Image img = Toolkit.getDefaultToolkit().createImage(prod);
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		return resizedImage;
	}
	
	public static VolatileImage createVolatileImage(int width, int height)
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleVolatileImage(width, height, Transparency.TRANSLUCENT);
	}
	
	public static FontMetrics getFontMetrics(Font font)
	{
		return canvas.getFontMetrics(font);
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
	
	public static int scale(double size)
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
	
	public static List<String> splitString(String str, Font font, int lineWidth)
	{
		return splitString(str, font, lineWidth, true);
	}
	
	public static List<String> splitString(String str, Font font, int lineWidth, boolean wrap)
	{
		FontMetrics metrics = getFontMetrics(font);
		List<String> lines = new ArrayList<String>();
		String line = "";
		char[] chars = str.toCharArray();
		// Iterate through all characters in the string
		for (int i = 0 ; i < chars.length ; i++)
		{
			char c = chars[i];
			// If the width of the string with the new character is greater than the allowed width
			if (metrics.stringWidth(line + c) > lineWidth)
			{
				if (wrap && c == ' ')
				{
					lines.add(line);
					line = "";
					continue;
				}
				else if (wrap)
				{
					char[] lineChars = line.toCharArray();
					boolean foundSpace = false;
					for (int e = lineChars.length - 1 ; e >= 0 ; e--)
					{
						if (lineChars[e] == ' ')
						{
							lines.add(line.substring(0, e));
							line = line.substring(e + 1);
							if (line.startsWith(" "))
							{
								line = line.substring(1);
							}
							foundSpace = true;
							break;
						}
					}
					if (!foundSpace)
					{
						lines.add(line);
						line = "";
					}
				}
				else
				{
					lines.add(line);
					line = "";
				}
			}
			line += c;
		}
		if (!line.equals(""))
		{
			lines.add(line);
		}
		return lines;
	}
	
	public static List<String> splitStrings(List<String> strs, Font font, int lineWidth, boolean wrap)
	{
		List<String> lines = new ArrayList<String>();
		for (String str : strs)
		{
			lines.addAll(splitString(str, font, lineWidth, wrap));
		}
		return lines;
	}
	
	public static Color getLighterColor(Color color, double lightFactor)
	{
		int[] origRgb = new int[] {color.getRed(), color.getGreen(), color.getBlue()};
		int[] rgb = new int[3];
		for (int i = 0 ; i < origRgb.length ; i++)
		{
			rgb[i] = (int) ((origRgb[i] + (255 * lightFactor)) / (lightFactor + 1));
		}
		
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
	
	public static Color getDarkerColor(Color color, double darkFactor)
	{
		int[] origRgb = new int[] {color.getRed(), color.getGreen(), color.getBlue()};
		int[] rgb = new int[3];
		for (int i = 0 ; i < origRgb.length ; i++)
		{
			rgb[i] = (int) (origRgb[i] - Math.min((255 - origRgb[i]) * darkFactor, origRgb[i]));
			//rgb[i] = (int) ((origRgb[i] + (255 * darkFactor)) / (darkFactor + 1));
		}
		
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
	
	public static Color getColorBetween(Color c1, Color c2, double progress)
	{
		int[] c1Rgb = new int[] {c1.getRed(), c1.getGreen(), c1.getBlue()};
		int[] c2Rgb = new int[] {c2.getRed(), c2.getGreen(), c2.getBlue()};
		int[] rgb = new int[3];
		for (int i = 0 ; i < 3 ; i++)
		{
			rgb[i] = (int) ((c1Rgb[i] * -(progress - 1)) + (c2Rgb[i] * progress));
		}
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
}
