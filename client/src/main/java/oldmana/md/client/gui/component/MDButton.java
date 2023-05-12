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

import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.HoverHelper;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.common.playerui.Button;
import oldmana.md.common.playerui.ButtonColorScheme;

public class MDButton extends MDComponent implements Button
{
	private String text;
	private int fontSize = 18;
	
	private ButtonColorScheme colors;
	
	private HoverHelper hover;
	
	private MouseAdapter listener;
	
	public MDButton(String text)
	{
		this.text = text;
		setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 16));
		colors = ButtonColorScheme.NORMAL;
		hover = new HoverHelper(this, 250);
	}
	
	public void setFontSize(int size)
	{
		this.fontSize = size;
	}
	
	@Override
	public String getText()
	{
		return text;
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
	}
	
	@Override
	public ButtonColorScheme getColor()
	{
		return colors;
	}
	
	@Override
	public void setColor(ButtonColorScheme colors)
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
		
		Color color = isEnabled() ? GraphicsUtils.getColorBetween(colors.color, colors.hoveredColor, hover.getHighlight()) : colors.disabledColor;
		Color shineColor = GraphicsUtils.getLighterColor(color, colors.topLightFactor);
		Color insideBorderColor = GraphicsUtils.getLighterColor(color, colors.innerBorderLightFactor);
		
		int borderWidth = (int) Math.max(GraphicsUtils.SCALE + 0.3, 1); // Start scaling the border at 1.7x scale
		
		// Draw Outline
		g.setColor(isEnabled() ? GraphicsUtils.getColorBetween(colors.outlineColor, colors.hoveredOutlineColor, hover.getHighlight()) : colors.disabledOutlineColor);
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
		g.setColor(isEnabled() ? (hover.isHovered() ? colors.hoveredTextColor : colors.textColor) : colors.disabledTextColor);
		g.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(fontSize)));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		TextPainter tp = new TextPainter(text, g.getFont(), new Rectangle(0, 0, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
}
