package oldmana.md.client.gui.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oldmana.md.client.card.Card;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDLabel;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.component.MDButton.ButtonColorScheme;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class IngameMenuScreen extends MDComponent
{
	private MDLabel menuText;
	
	private MDButton quit;
	private MDButton resume;
	
	private MDText uiScaleText;
	private MDText uiScale;
	private MDButton enlargeUI;
	private MDButton shrinkUI;
	
	private MDButton debug;
	
	public IngameMenuScreen()
	{
		setVisible(false);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
			{
				if (getClient().getTableScreen().isVisible())
				{
					if (!getClient().getTableScreen().getChat().isChatOpen() && !isVisible() && event.getID() == KeyEvent.KEY_PRESSED)
					{
						if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
						{
							setVisible(true);
							requestFocus();
							return true;
						}
					}
				}
				return false;
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				if (!isVisible())
				{
					return;
				}
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					setVisible(false);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {});
		
		Color whiteish = new Color(230, 230, 230);
		
		menuText = new MDLabel("Menu");
		menuText.setSize(50);
		add(menuText);
		
		quit = new MDButton("Leave Game");
		quit.setFontSize(26);
		quit.setColorScheme(ButtonColorScheme.OLD);
		quit.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				setVisible(false);
				getClient().disconnect("quitting");
			}
		});
		add(quit);
		
		resume = new MDButton("Return to Game");
		resume.setFontSize(26);
		resume.setColorScheme(ButtonColorScheme.OLD);
		resume.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				setVisible(false);
			}
		});
		add(resume);
		
		uiScaleText = new MDText("UI Scale");
		uiScaleText.setFontSize(30);
		uiScaleText.setHorizontalAlignment(Alignment.CENTER);
		uiScaleText.setColor(whiteish);
		uiScaleText.setBold(true);
		add(uiScaleText);
		
		enlargeUI = new MDButton("+");
		enlargeUI.setFontSize(26);
		enlargeUI.setColorScheme(ButtonColorScheme.OLD);
		enlargeUI.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				GraphicsUtils.setScale(Math.min(GraphicsUtils.SCALE + 0.1, 4.0));
				uiScale.setText(((int) (GraphicsUtils.SCALE * 100)) + "%");
				getClient().getTableScreen().revalidate();
				getClient().getTableScreen().repaint();
				
				getClient().getGameState().updateUI();
				revalidate();
			}
		});
		add(enlargeUI);
		
		shrinkUI = new MDButton("-");
		shrinkUI.setFontSize(26);
		shrinkUI.setColorScheme(ButtonColorScheme.OLD);
		shrinkUI.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				GraphicsUtils.setScale(Math.max(GraphicsUtils.SCALE - 0.1, 0.5));
				uiScale.setText(((int) (GraphicsUtils.SCALE * 100)) + "%");
				getClient().getTableScreen().revalidate();
				getClient().getTableScreen().repaint();
				
				getClient().getGameState().updateUI();
				revalidate();
			}
		});
		add(shrinkUI);
		
		uiScale = new MDText(((int) (GraphicsUtils.SCALE * 100)) + "%");
		uiScale.setFontSize(28);
		uiScale.setHorizontalAlignment(Alignment.CENTER);
		uiScale.setVerticalAlignment(Alignment.CENTER);
		uiScale.setColor(whiteish);
		add(uiScale);
		
		debug = new MDButton("Debug");
		debug.setFontSize(16);
		debug.setColorScheme(ButtonColorScheme.OLD);
		debug.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				getClient().setDebugEnabled(!getClient().isDebugEnabled());
				for (Card card : Card.getRegisteredCards().values())
				{
					card.clearGraphicsCache();
				}
				repaint();
			}
		});
		add(debug);
		
		setLayout(new IngameMenuLayout());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public class IngameMenuLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			menuText.setLocation((getWidth() / 2) - scale(100), scale(30));
			menuText.setSize(scale(200), scale(52));
			
			uiScaleText.setLocation((getWidth() / 2) - scale(150), menuText.getMaxY() + scale(40));
			uiScaleText.setSize(scale(300), scale(32));
			
			uiScale.setSize(scale(80), scale(32));
			uiScale.setLocationCentered(getWidth() / 2, uiScaleText.getMaxY() + scale(24));
			enlargeUI.setSize(scale(30), scale(30));
			enlargeUI.setLocationCentered(uiScale.getX() - scale(16), uiScaleText.getMaxY() + scale(24));
			shrinkUI.setSize(scale(30), scale(30));
			shrinkUI.setLocationCentered(uiScale.getMaxX() + scale(16), uiScaleText.getMaxY() + scale(24));
			
			debug.setSize(scale(80), scale(30));
			debug.setLocation(getWidth() - scale(85), scale(5));
			
			
			resume.setSize(scale(320), scale(60));
			resume.setLocation((getWidth() / 2) - scale(160), Math.max((getHeight() / 2) - scale(30), uiScale.getMaxY() + scale(10)));
			quit.setSize(scale(320), scale(60));
			quit.setLocation(resume.getX(), resume.getMaxY() + scale(20));
		}
		
		@Override
		public void invalidateLayout(Container container)
		{
			layoutContainer(container);
		}
	}
}
