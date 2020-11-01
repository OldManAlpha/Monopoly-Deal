package oldmana.general.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.gui.util.TextPainter;
import oldmana.general.md.client.gui.util.TextPainter.Alignment;

public class MDButton extends MDComponent
{
	public static Color COLOR_NORMAL = Color.LIGHT_GRAY;
	public static Color COLOR_NORMAL_HOVER = new Color(205, 230, 255);
	public static Color COLOR_ALERT = new Color(255, 100, 100);
	
	private String text;
	private int fontSize = 18;
	
	private ButtonColorScheme colors;
	
	private boolean hovered;
	
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
	}
	
	public void setFontSize(int size)
	{
		this.fontSize = size;
		//setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, size));
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
		addMouseListener(listener);
		this.listener = listener;
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
		g.setColor(isEnabled() ? (hovered ? colors.hoveredColor : colors.color) : colors.disabledColor);
		g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(isEnabled() ? (hovered ? colors.hoveredOutlineColor : colors.outlineColor) : colors.disabledOutlineColor);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g.setColor(isEnabled() ? (hovered ? colors.hoveredTextColor : colors.textColor) : colors.disabledTextColor);
		g.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, scale(fontSize)));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		TextPainter tp = new TextPainter(text, g.getFont(), new Rectangle(0, 0, getWidth(), getHeight()));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
	
	
	public static enum ButtonColorScheme
	{
		NORMAL(new Color(190, 190, 190), new Color(100, 100, 100), new Color(50, 50, 50), new Color(160, 190, 220), new Color(70, 100, 160), new Color(50, 50, 50),
				new Color(200, 200, 200), new Color(150, 150, 150), new Color(120, 120, 120)),
		ALERT(new Color(240, 100, 100), new Color(150, 80, 80), new Color(20, 20, 20), new Color(240, 130, 130), new Color(180, 100, 100), new Color(20, 20, 20),
				new Color(255, 160, 160), new Color(240, 120, 120), new Color(120, 120, 120));
		
		public Color color;
		public Color outlineColor;
		public Color textColor;
		
		public Color hoveredColor;
		public Color hoveredOutlineColor;
		public Color hoveredTextColor;
		
		public Color disabledColor;
		public Color disabledOutlineColor;
		public Color disabledTextColor;
		
		ButtonColorScheme(Color color, Color outlineColor, Color textColor, Color hoveredColor, Color hoveredOutlineColor, Color hoveredTextColor, 
				Color disabledColor, Color disabledOutlineColor, Color disabledTextColor)
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
		}
	}
}
