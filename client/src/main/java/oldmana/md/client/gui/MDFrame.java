package oldmana.md.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import oldmana.md.client.MDClient;
import oldmana.md.client.gui.screen.FirstRunScreen;
import oldmana.md.client.gui.screen.MainMenuScreen;
import oldmana.md.client.gui.screen.TableScreen;
import oldmana.md.client.gui.util.CardPainter;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDFrame extends JFrame
{
	private JComponent visibleScreen;
	
	private TableScreen tableScreen;
	private MainMenuScreen menuScreen;
	private FirstRunScreen firstRunScreen;
	
	private List<BufferedImage> normalIcons;
	private List<BufferedImage> alertIcons;
	
	private Timer flashTimer;
	
	public MDFrame(boolean firstRun)
	{
		super("Monopoly Deal");
		GraphicsUtils.setScale(MDClient.getInstance().getSettings().getDouble("scale"), false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				MDClient.getInstance().disconnect("closing", true);
			}
		});
		setLayout(new LayoutAdapter()
		{
			@Override
			public void layoutContainer(Container parent)
			{
				for (Component c : parent.getComponents())
				{
					c.setSize(getContentPane().getSize());
				}
			}
		});
		setMinimumSize(new Dimension(300, 400));
		tableScreen = new TableScreen();
		tableScreen.setVisible(false);
		menuScreen = new MainMenuScreen();
		menuScreen.setVisible(false);
		firstRunScreen = new FirstRunScreen();
		firstRunScreen.setVisible(false);
		getContentPane().add(tableScreen);
		getContentPane().add(menuScreen);
		getContentPane().add(firstRunScreen);
		setVisible(true);
		if (firstRun)
		{
			displayFirstRunScreen();
		}
		else
		{
			displayMenu();
		}
		
		normalIcons = new ArrayList<BufferedImage>();
		alertIcons = new ArrayList<BufferedImage>();
		
		for (int i = 0 ; i < 4 ; i++)
		{
			int size = 32 << i;
			BufferedImage icon = GraphicsUtils.createImage(size, size);
			Graphics2D g = icon.createGraphics();
			g.translate(3 << i, 2 << i);
			drawIconCardBack(g, 18 << i, 27 << i);
			g.rotate(Math.toRadians(10), 10 << i, 25 << i);
			g.translate(5 << i, 0);
			drawIconCardBack(g, 18 << i, 27 << i);
			g.dispose();
			normalIcons.add(icon);
			BufferedImage alertIcon = GraphicsUtils.createImage(size, size);
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
	
	private void drawIconCardBack(Graphics2D g, int width, int height)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// Draw White
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, width - 1, height - 1, width / 6, height / 6);
		// Draw Red
		g.setColor(new Color(239, 15, 20));
		g.fillRect(width / 12, width / 12, width - ((width / 12) * 2) - 1, height - ((width / 12) * 2) - 1);
		//g.drawImage(CardPainter.backGraphic.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), width / 12,
		// width / 12, width - ((width / 12) * 2) - 1, height - ((width / 12) * 2) - 1, null);
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
		TextPainter tp = new TextPainter("!", GraphicsUtils.getBoldMDFont((int) (width / 1.5)), new Rectangle(0, height / 10, width, height));
		tp.setHorizontalAlignment(Alignment.CENTER);
		tp.setVerticalAlignment(Alignment.CENTER);
		tp.paint(g);
	}
	
	private void setScreen(JComponent screen)
	{
		if (visibleScreen != null)
		{
			visibleScreen.setVisible(false);
		}
		screen.setVisible(true);
		visibleScreen = screen;
	}
	
	public void displayTable()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		getContentPane().setPreferredSize(new Dimension((int) Math.min(GraphicsUtils.scale(1600), screen.getWidth() - 200), 
				(int) Math.min(GraphicsUtils.scale(900), screen.getHeight() - 180)));
		pack();
		centerWindow();
		setScreen(tableScreen);
	}
	
	public void displayMenu()
	{
		getContentPane().setPreferredSize(new Dimension(GraphicsUtils.scale(800), GraphicsUtils.scale(500)));
		pack();
		centerWindow();
		setScreen(menuScreen);
	}
	
	public void displayFirstRunScreen()
	{
		getContentPane().setPreferredSize(new Dimension(GraphicsUtils.scale(800), GraphicsUtils.scale(500)));
		pack();
		centerWindow();
		setScreen(firstRunScreen);
	}
	
	private Point getScreenCenter()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
	}
	
	public void centerWindow()
	{
		Point center = getScreenCenter();
		setLocation(center.x - (getWidth() / 2), center.y - (getHeight() / 2));
	}
	
	public TableScreen getTableScreen()
	{
		return tableScreen;
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(600, 400);
	}
}
