package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.MDScheduler;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDButton extends MDComponent
{
	public static Color COLOR_NORMAL = Color.LIGHT_GRAY;
	public static Color COLOR_NORMAL_HOVER = new Color(205, 230, 255);
	public static Color COLOR_ALERT = new Color(255, 100, 100);
	
	private String text;
	private int fontSize = 18;
	
	private ButtonColorScheme colors;
	
	private boolean hovered;
	private double highlight;
	
	private MouseAdapter listener;
	
	public MDButton(String text)
	{
		this.text = text;
		setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 16));
		colors = ButtonColorScheme.NORMAL;
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				hovered = true;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent event)
			{
				hovered = false;
				repaint();
			}
		});
		
		getClient().getScheduler().scheduleFrameboundTask(task ->
		{
			if (!isDisplayable())
			{
				task.cancel();
				return;
			}
			if (hovered && highlight < 1 && isEnabled())
			{
				highlight = Math.min(highlight + (MDScheduler.getFrameDelay() / 250), 1);
				repaint();
			}
			else if ((!hovered && highlight > 0) || !isEnabled())
			{
				highlight = Math.max(highlight - (MDScheduler.getFrameDelay() / 250), 0);
				repaint();
			}
		});
	}
	
	public void setFontSize(int size)
	{
		this.fontSize = size;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setColorScheme(ButtonColorScheme colors)
	{
		this.colors = colors;
	}
	
	public void setListener(MouseAdapter listener)
	{
		removeListener();
		if (listener != null)
		{
			addMouseListener(listener);
		}
		this.listener = listener;
	}
	
	public void setListener(Runnable task)
	{
		setListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				task.run();
			}
		});
	}
	
	public void removeListener()
	{
		if (listener != null)
		{
			removeMouseListener(listener);
			listener = null;
		}
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color color = isEnabled() ? GraphicsUtils.getColorBetween(colors.color, colors.hoveredColor, highlight) : colors.disabledColor;
		Color shineColor = GraphicsUtils.getLighterColor(color, colors.topLightFactor);
		Color insideBorderColor = GraphicsUtils.getLighterColor(color, colors.innerBorderLightFactor);
		
		int borderWidth = (int) Math.max(GraphicsUtils.SCALE + 0.3, 1); // Start scaling the border at 1.7x scale
		
		// Draw Outline
		g.setColor(isEnabled() ? GraphicsUtils.getColorBetween(colors.outlineColor, colors.hoveredOutlineColor, highlight) : colors.disabledOutlineColor);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(7), scale(7));
		
		// Draw Inside Border
		g.setColor(insideBorderColor);
		g.fillRoundRect(borderWidth, borderWidth, getWidth() - (borderWidth * 2) - 1, getHeight() - (borderWidth * 2) - 1, scale(5), scale(5));
		
		int lightHeight = (int) (0.48 * getHeight());
		
		// Draw bottom gradient
		GradientPaint paint = new GradientPaint(0, getHeight() / 2, GraphicsUtils.getLighterColor(color, colors.bottomLightGradient), 0, getHeight(), color);
		g.setColor(color);
		g.setPaint(paint);
		g.fillRoundRect((borderWidth * 2), lightHeight, getWidth() - (borderWidth * 4) - 1, getHeight() - lightHeight - (borderWidth * 2) - 1, scale(5), scale(5));
		g.fillRect((borderWidth * 2), lightHeight, getWidth() - (borderWidth * 4) - 1, scale(6));
		
		// Draw top gradient
		g.setColor(shineColor);
		paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(color, colors.topLightFactor + colors.topLightGradient), 0, getHeight() / 2, 
				GraphicsUtils.getLighterColor(color, colors.topLightFactor));
		g.setPaint(paint);
		g.fillRoundRect((borderWidth * 2), (borderWidth * 2), getWidth() - (borderWidth * 4) - 1, lightHeight - (borderWidth * 2) - 1, scale(5), scale(5));
		g.fillRect((borderWidth * 2), lightHeight - scale(6), getWidth() - (borderWidth * 4) - 1, scale(6));
		
		// Draw Text
		g.setColor(isEnabled() ? (hovered ? colors.hoveredTextColor : colors.textColor) : colors.disabledTextColor);
		g.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(fontSize)));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		TextPainter tp = new TextPainter(text, g.getFont(), new Rectangle(0, 0, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
	
	
	public enum ButtonColorScheme
	{
		NORMAL(new Color(212, 212, 212), new Color(112, 112, 112), new Color(0, 0, 0), 
				new Color(167, 217, 245), new Color(60, 127, 177), new Color(0, 0, 0),
				new Color(230, 230, 230), new Color(160, 160, 160), new Color(120, 120, 120), 
				0.35, 1.2, 0.9, 3),
		ALERT(new Color(240, 100, 100), new Color(150, 80, 80), new Color(0, 0, 0), 
				new Color(240, 130, 130), new Color(180, 100, 100), new Color(0, 0, 0),
				new Color(255, 160, 160), new Color(240, 120, 120), new Color(120, 120, 120), 
				0.1, 0.3, 0.1, 1),
		OLD(new Color(190, 190, 190), new Color(100, 100, 100), new Color(0, 0, 0), 
				new Color(160, 190, 220), new Color(70, 100, 160), new Color(0, 0, 0),
				new Color(230, 230, 230), new Color(160, 160, 160), new Color(120, 120, 120), 
				0.2, 0.5, 0.4, 1.5);
		
		public Color color;
		public Color outlineColor;
		public Color textColor;
		
		public Color hoveredColor;
		public Color hoveredOutlineColor;
		public Color hoveredTextColor;
		
		public Color disabledColor;
		public Color disabledOutlineColor;
		public Color disabledTextColor;
		
		public double bottomLightGradient;
		public double topLightFactor;
		public double topLightGradient;
		public double innerBorderLightFactor;
		
		ButtonColorScheme(Color color, Color outlineColor, Color textColor, Color hoveredColor, Color hoveredOutlineColor, Color hoveredTextColor, 
				Color disabledColor, Color disabledOutlineColor, Color disabledTextColor, double bottomLightGradient, double topLightFactor, 
				double topLightGradient, double innerBorderLightFactor)
		{
			this.color = color;
			this.outlineColor = outlineColor;
			this.textColor = textColor;
			
			this.hoveredColor = hoveredColor;
			this.hoveredOutlineColor = hoveredOutlineColor;
			this.hoveredTextColor = hoveredTextColor;
			
			this.disabledColor = disabledColor;
			this.disabledOutlineColor = disabledOutlineColor;
			this.disabledTextColor = disabledTextColor;
			
			this.bottomLightGradient = bottomLightGradient;
			this.topLightFactor = topLightFactor;
			this.topLightGradient = topLightGradient;
			this.innerBorderLightFactor = innerBorderLightFactor;
		}
		
		public static ButtonColorScheme fromID(int id)
		{
			for (ButtonColorScheme colors : values())
			{
				if (colors.ordinal() == id)
				{
					return colors;
				}
			}
			return values()[0];
		}
	}
}
