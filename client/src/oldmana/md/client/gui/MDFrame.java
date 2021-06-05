package oldmana.md.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import oldmana.md.client.gui.screen.MainMenuScreen;
import oldmana.md.client.gui.screen.TableScreen;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDFrame extends JFrame
{
	private TableScreen tableScreen;
	
	private List<BufferedImage> normalIcons;
	private List<BufferedImage> alertIcons;
	
	private Timer flashTimer;
	
	public MDFrame()
	{
		super("Monopoly Deal");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new LayoutManager()
		{
			@Override
			public void addLayoutComponent(String name, Component comp) {}
			
			@Override
			public void layoutContainer(Container parent)
			{
				for (Component c : parent.getComponents())
				{
					c.setSize(getContentPane().getSize());
				}
			}
			
			@Override
			public Dimension minimumLayoutSize(Container parent)
			{
				return new Dimension(200, 200);
			}
			
			@Override
			public Dimension preferredLayoutSize(Container parent) { return null; }
			
			@Override
			public void removeLayoutComponent(Component comp) {}
		});
		getContentPane().setPreferredSize(new Dimension(600, 400));
		pack();
		setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth()) / 2, 
				(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight()) / 2);
		tableScreen = new TableScreen();
		MainMenuScreen menu = new MainMenuScreen();
		getContentPane().add(menu);
		setVisible(true);
		
		normalIcons = new ArrayList<BufferedImage>();
		alertIcons = new ArrayList<BufferedImage>();
		
		for (int i = 0 ; i < 4 ; i++)
		{
			BufferedImage icon = GraphicsUtils.createImage(power(32, i), power(32, i));
			Graphics2D g = icon.createGraphics();
			g.translate(power(3, i), power(2, i));
			drawIconCardBack(g, power(18, i), power(27, i));
			g.rotate(Math.toRadians(10), power(10, i), power(25, i));
			g.translate(power(5, i), 0);
			drawIconCardBack(g, power(18, i), power(27, i));
			g.dispose();
			normalIcons.add(icon);
			BufferedImage alertIcon = GraphicsUtils.createImage(power(32, i), power(32, i));
			icon.copyData(alertIcon.getRaster());
			g = alertIcon.createGraphics();
			drawAlert(g, alertIcon.getWidth(), alertIcon.getHeight());
			alertIcons.add(alertIcon);
		}
		setIconImages(normalIcons);
	}
	
	public void setAlert(boolean alert)
	{
		if (alert && flashTimer == null)
		{
			flashTimer = new Timer(500, new ActionListener()
			{
				boolean flashOn = false;
				@Override
				public void actionPerformed(ActionEvent event)
				{
					flashOn = !flashOn;
					if (isFocused())
					{
						flashTimer.stop();
						flashTimer = null;
						flashOn = false;
					}
					if (flashOn)
					{
						setIconImages(alertIcons);
					}
					else
					{
						setIconImages(normalIcons);
					}
				}
			});
			flashTimer.start();
		}
		else if (!alert && flashTimer != null)
		{
			setIconImages(normalIcons);
			flashTimer.stop();
			flashTimer = null;
		}
	}
	
	private int power(int num, int i)
	{
		for (int e = 0 ; e < i ; e++)
		{
			num *= 2;
		}
		return num;
	}
	
	private void drawIconCardBack(Graphics2D g, int width, int height)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// Draw White
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, width - 1, height - 1, width / 6, height / 6);
		
		g.setColor(Color.BLACK);
		//g.drawRoundRect(0, 0, width - 1, height - 1, width / 6, height / 6);
		g.setColor(new Color(239, 15, 20));
		g.fillRect(width / 12, width / 12, width - ((width / 12) * 2) - 1, height - ((width / 12) * 2) - 1);
		// Draw Inner Outline
		g.setColor(Color.BLACK);
		//g.drawRoundRect(width / 15, width / 15, width - ((width / 15) * 2) - 1, height - ((width / 15) * 2) - 1, width / 6, width / 6);
	}
	
	private void drawAlert(Graphics2D g, int width, int height)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.setColor(Color.WHITE);
		g.fillOval((int) (width * 0.2), (int) (width * 0.2), (int) (width * 0.6), (int) (width * 0.6));
		g.setColor(Color.BLACK);
		g.drawOval((int) (width * 0.2), (int) (width * 0.2), (int) (width * 0.6), (int) (width * 0.6));
		g.setColor(Color.RED);
		TextPainter tp = new TextPainter("!", GraphicsUtils.getBoldMDFont((int) (width / 1.5)), new Rectangle(0, height / 20, width, height));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
	
	public TableScreen getTableScreen()
	{
		return tableScreen;
	}
}
