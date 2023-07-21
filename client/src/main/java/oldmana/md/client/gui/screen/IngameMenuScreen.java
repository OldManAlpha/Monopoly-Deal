package oldmana.md.client.gui.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import oldmana.md.client.Player;
import oldmana.md.client.Scheduler;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDLabel;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.common.playerui.ButtonColorScheme;

public class IngameMenuScreen extends MDComponent
{
	private MDLabel menuText;
	
	private MDButton quit;
	private MDButton resume;
	
	private MDText uiScaleText;
	private MDText uiScale;
	private MDButton enlargeUI;
	private MDButton shrinkUI;
	
	private MDText fpsText;
	private MDText fps;
	private MDButton increaseFPS;
	private MDButton decreaseFPS;
	
	private List<String> tips = new ArrayList<String>(Arrays.asList(
			"The chat can be opened by pressing the 'T' key",
			"This menu can also be opened by pressing the Escape key",
			"Cards in your hand can be rearranged by dragging them around",
			"See the rules for the deck you're playing by using the '/rules' command",
			"You can scroll through the discard pile to see what cards have been played",
			"Make sure to play Double The Rent before the Rent card!",
			"You can hover over the cards in your opponent's bank to see what's in it"));
	{
		Collections.shuffle(tips);
	}
	private int lastTip = ThreadLocalRandom.current().nextInt(tips.size());
	
	private MDText tip;
	
	private MDButton debug;
	
	public IngameMenuScreen()
	{
		setVisible(false);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event ->
		{
			if (getClient().getTableScreen().isVisible())
			{
				if (!getClient().getTableScreen().getChat().isChatOpen() && !isVisible() && event.getID() == KeyEvent.KEY_PRESSED)
				{
					if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						if (getClient().getTableScreen().getActionScreen() != null && getClient().getTableScreen().getActionScreen().isVisible())
						{
							if (getClient().getGameState().getClientActionState() != null)
							{
								getClient().getGameState().setClientActionState(null);
							}
							else
							{
								getClient().getTableScreen().getActionScreen().setVisible(false);
							}
							return true;
						}
						setVisible(true);
						requestFocus();
						return true;
					}
				}
			}
			return false;
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
		menuText.setPadding(60);
		menuText.setSize(50);
		add(menuText);
		
		quit = new MDButton("Leave Game");
		quit.setFontSize(26);
		quit.setColor(ButtonColorScheme.GRAY);
		quit.addClickListener(() ->
		{
			setVisible(false);
			getClient().disconnect("quitting");
		});
		add(quit);
		
		resume = new MDButton("Return to Game");
		resume.setFontSize(26);
		resume.setColor(ButtonColorScheme.GRAY);
		resume.addClickListener(() -> setVisible(false));
		add(resume);
		
		uiScaleText = new MDText("UI Scale");
		uiScaleText.setFontSize(30);
		uiScaleText.setHorizontalAlignment(Alignment.CENTER);
		uiScaleText.setColor(whiteish);
		uiScaleText.setBold(true);
		uiScaleText.setOutlineThickness(4);
		add(uiScaleText);
		
		enlargeUI = new MDButton("+");
		enlargeUI.setFontSize(26);
		enlargeUI.setColor(ButtonColorScheme.GRAY);
		enlargeUI.addClickListener(() ->
		{
			GraphicsUtils.setScale(Math.min(GraphicsUtils.SCALE + 0.1, 4.0));
			uiScale.setText(((int) (GraphicsUtils.SCALE * 100)) + "%");
			getClient().getTableScreen().revalidate();
			getClient().getTableScreen().repaint();
			
			getClient().getGameState().updateUI();
			revalidate();
			
			getClient().getTableScreen().getChat().recalculateLines();
		});
		add(enlargeUI);
		
		shrinkUI = new MDButton("-");
		shrinkUI.setFontSize(26);
		shrinkUI.setColor(ButtonColorScheme.GRAY);
		shrinkUI.addClickListener(() ->
		{
			GraphicsUtils.setScale(Math.max(GraphicsUtils.SCALE - 0.1, 0.5));
			uiScale.setText(((int) (GraphicsUtils.SCALE * 100)) + "%");
			getClient().getTableScreen().revalidate();
			getClient().getTableScreen().repaint();
			
			getClient().getGameState().updateUI();
			revalidate();
			
			getClient().getTableScreen().getChat().recalculateLines();
		});
		add(shrinkUI);
		
		uiScale = new MDText(((int) (GraphicsUtils.SCALE * 100)) + "%");
		uiScale.setFontSize(28);
		uiScale.setHorizontalAlignment(Alignment.CENTER);
		uiScale.setVerticalAlignment(Alignment.CENTER);
		uiScale.setColor(whiteish);
		uiScale.setOutlineThickness(3);
		add(uiScale);
		
		
		fpsText = new MDText("Framerate");
		fpsText.setFontSize(30);
		fpsText.setHorizontalAlignment(Alignment.CENTER);
		fpsText.setColor(whiteish);
		fpsText.setBold(true);
		fpsText.setOutlineThickness(4);
		add(fpsText);
		
		increaseFPS = new MDButton("+");
		increaseFPS.setFontSize(26);
		increaseFPS.setColor(ButtonColorScheme.GRAY);
		increaseFPS.addClickListener(() ->
		{
			getClient().getScheduler().setFPS(Math.min(Scheduler.getFPS() + 5, 500), true);
			fps.setText(Scheduler.getFPS() + " FPS");
			getClient().getTableScreen().revalidate();
			getClient().getTableScreen().repaint();
			
			getClient().getGameState().updateUI();
			revalidate();
		});
		add(increaseFPS);
		
		decreaseFPS = new MDButton("-");
		decreaseFPS.setFontSize(26);
		decreaseFPS.setColor(ButtonColorScheme.GRAY);
		decreaseFPS.addClickListener(() ->
		{
			getClient().getScheduler().setFPS(Math.max(Scheduler.getFPS() - 5, 5), true);
			fps.setText(Scheduler.getFPS() + " FPS");
			getClient().getTableScreen().revalidate();
			getClient().getTableScreen().repaint();
			
			getClient().getGameState().updateUI();
			revalidate();
		});
		add(decreaseFPS);
		
		fps = new MDText(Scheduler.getFPS() + " FPS");
		fps.setFontSize(28);
		fps.setHorizontalAlignment(Alignment.CENTER);
		fps.setVerticalAlignment(Alignment.CENTER);
		fps.setColor(whiteish);
		fps.setOutlineThickness(3);
		add(fps);
		
		
		tip = new MDText("");
		tip.setFontSize(24);
		tip.setHorizontalAlignment(Alignment.CENTER);
		tip.setColor(whiteish);
		tip.setBold(true);
		tip.setOutlineThickness(4);
		add(tip);
		
		
		debug = new MDButton("Debug");
		debug.setFontSize(16);
		debug.setColor(ButtonColorScheme.GRAY);
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
				getClient().getDeck().getUI().updateGraphics();
				getClient().getDiscardPile().getUI().updateGraphics();
				for (Player player : getClient().getAllPlayers())
				{
					player.getHand().getUI().updateGraphics();
					player.getBank().getUI().updateGraphics();
					player.getUI().getPropertySets().updateGraphics();
					for (PropertySet set : player.getPropertySets())
					{
						set.getUI().updateGraphics();
					}
				}
			}
		});
		add(debug);
		
		setLayout(new IngameMenuLayout());
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			lastTip = (lastTip + 1) % tips.size();
			tip.setText("Tip: " + tips.get(lastTip));
		}
	}
	
	@Override
	public void doPaint(Graphics g)
	{
		super.doPaint(g);
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public class IngameMenuLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			menuText.setSize(scale(50));
			menuText.setLocationCentered((getWidth() / 2), scale(60));
			//menuText.setSize(scale(200), scale(52));
			
			uiScaleText.setLocation((getWidth() / 2) - scale(150), menuText.getMaxY() + scale(40));
			uiScaleText.setSize(scale(300), scale(32));
			
			uiScale.setSize(scale(80), scale(32));
			uiScale.setLocationCentered(getWidth() / 2, uiScaleText.getMaxY() + scale(15));
			enlargeUI.setSize(scale(30), scale(30));
			enlargeUI.setLocationCentered(uiScale.getMaxX() + scale(16), uiScaleText.getMaxY() + scale(15));
			shrinkUI.setSize(scale(30), scale(30));
			shrinkUI.setLocationCentered(uiScale.getX() - scale(16), uiScaleText.getMaxY() + scale(15));
			
			
			fpsText.setLocation((getWidth() / 2) - scale(150), menuText.getMaxY() + scale(120));
			fpsText.setSize(scale(300), scale(32));
			
			fps.setSize(scale(90), scale(32));
			fps.setLocationCentered(getWidth() / 2, fpsText.getMaxY() + scale(15));
			increaseFPS.setSize(scale(30), scale(30));
			increaseFPS.setLocationCentered(fps.getMaxX() + scale(16), fpsText.getMaxY() + scale(15));
			decreaseFPS.setSize(scale(30), scale(30));
			decreaseFPS.setLocationCentered(fps.getX() - scale(16), fpsText.getMaxY() + scale(15));
			
			debug.setSize(scale(80), scale(30));
			debug.setLocation(getWidth() - scale(85), scale(5));
			
			
			resume.setSize(scale(320), scale(60));
			resume.setLocation((getWidth() / 2) - scale(160), Math.max((getHeight() / 2) - scale(30), fps.getMaxY() + scale(10)));
			quit.setSize(scale(320), scale(60));
			quit.setLocation(resume.getX(), resume.getMaxY() + scale(20));
			
			tip.setSize(getWidth(), scale(40));
			tip.setLocation(0, getHeight() - scale(40));
		}
		
		@Override
		public void invalidateLayout(Container container)
		{
			layoutContainer(container);
		}
	}
}
