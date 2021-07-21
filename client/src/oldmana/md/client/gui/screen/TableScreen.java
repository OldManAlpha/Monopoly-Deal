package oldmana.md.client.gui.screen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardRegistry;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.card.collection.VoidCollection;
import oldmana.md.client.gui.action.ActionScreen;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDChat;
import oldmana.md.client.gui.component.MDLayeredButton;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.component.MDTurns;
import oldmana.md.client.gui.component.MDUndoButton;
import oldmana.md.client.gui.component.collection.MDDeck;
import oldmana.md.client.gui.component.collection.MDDiscardPile;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.component.collection.MDVoidCollection;
import oldmana.md.client.gui.component.large.MDPlayer;
import oldmana.md.client.gui.component.large.MDTopbar;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class TableScreen extends JLayeredPane
{
	private MDTopbar topbar;
	private MDDeck deck;
	private MDDiscardPile discard;
	private MDHand hand;
	
	private MDVoidCollection voidCollection;
	
	private MDButton multiButton;
	private MDUndoButton undoButton;
	private MDTurns turnCount;
	
	private MDChat chat;
	
	private ActionScreen actionScreen;
	
	private MDButton debug;
	
	private MDText version;
	
	private MDButton enlargeUI;
	private MDButton shrinkUI;
	private MDText uiScale;
	
	public TableScreen()
	{
		super();
		setLayout(new TableLayout());
		setSize(new Dimension(1600, 900));
		
		hand = new MDHand(null);
		add(hand, new Integer(0));
		
		deck = new MDDeck(null);
		add(deck, new Integer(0));
		
		discard = new MDDiscardPile(null);
		add(discard, new Integer(0));
		
		voidCollection = new MDVoidCollection(null);
		add(voidCollection, new Integer(1));
		
		topbar = new MDTopbar();
		topbar.setText("");
		topbar.setSize(1600, 35);
		add(topbar, new Integer(0));
		undoButton = new MDUndoButton("Undo Card");
		undoButton.setLocation(10, 570);
		undoButton.setSize(180, 50);
		undoButton.setFontSize(24);
		add(undoButton, new Integer(0));
		
		multiButton = new MDLayeredButton("");
		multiButton.setLocation(10, 500);
		multiButton.setSize(180, 50);
		multiButton.setFontSize(24);
		multiButton.setEnabled(false);
		add(multiButton, new Integer(0));
		
		turnCount = new MDTurns();
		turnCount.setSize(181, 30);
		turnCount.setLocation(10, 460);
		add(turnCount, new Integer(0));
		
		version = new MDText("Version " + MDClient.VERSION);
		version.setLocation(5, 880);
		version.setSize(200, 15);
		version.setFontSize(16);
		add(version);
		
		debug = new MDButton("DB");
		debug.setLocation(1555, 5);
		debug.setSize(40, 25);
		debug.setFontSize(16);
		debug.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				MDClient.getInstance().setDebugEnabled(!MDClient.getInstance().isDebugEnabled());
				for (Card card : CardRegistry.getRegisteredCards())
				{
					card.clearGraphicsCache();
				}
				repaint();
			}
		});
		add(debug, new Integer(1));
		
		chat = new MDChat();
		chat.setSize(500, 400);
		chat.setLocation(50, 300);
		add(chat, new Integer(150));
		
		
		enlargeUI = new MDButton("+");
		enlargeUI.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				GraphicsUtils.SCALE = ((GraphicsUtils.SCALE * 10) + 1) / 10.0;
				uiScale.setText("" + GraphicsUtils.SCALE);
				revalidate();
				repaint();
				
				getClient().getGameState().updateUI();
			}
		});
		add(enlargeUI, new Integer(1));
		
		shrinkUI = new MDButton("-");
		shrinkUI.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent event)
			{
				GraphicsUtils.SCALE = Math.max(0.5, ((GraphicsUtils.SCALE * 10) - 1) / 10.0);
				uiScale.setText("" + GraphicsUtils.SCALE);
				revalidate();
				repaint();
				
				getClient().getGameState().updateUI();
			}
		});
		add(shrinkUI, new Integer(1));
		
		uiScale = new MDText("" + GraphicsUtils.SCALE);
		uiScale.setFontSize(20);
		uiScale.setHorizontalAlignment(Alignment.CENTER);
		add(uiScale, new Integer(1));
	}
	
	public void positionPlayers()
	{
		List<Player> ordered = getClient().getAllPlayers();
		ordered.sort(new Comparator<Player>()
		{
			@Override
			public int compare(Player p1, Player p2)
			{
				return p1.getID() - p2.getID();
			}
		});
		int thePlayerIndex = 0;
		for (int i = 0 ; i < ordered.size() ; i++)
		{
			if (ordered.get(i) instanceof ThePlayer)
			{
				thePlayerIndex = i;
			}
		}
		
		// 162
		
		int space = (getHeight() - hand.getHeight() - topbar.getHeight() - scale(15)) - scale((162 + 5) * 2);
		int padding = ordered.size() > 1 ? (space - (scale(162) * (ordered.size() - 2))) / (ordered.size() - 1) : 0;
		
		for (int i = 0 ; i < ordered.size() ; i++)
		{
			int index = (thePlayerIndex + i) % ordered.size();
			int loc = i == 0 ? 3 : i - 1;
			Player player = ordered.get(index);
			MDPlayer ui = player.getUI();
			if (loc == 0)
			{
				ui.setLocation(multiButton.getX() + multiButton.getWidth() + scale(10), topbar.getHeight() + scale(10));
			}
			else if (loc == 3)
			{
				ui.setLocation(multiButton.getX() + multiButton.getWidth() + scale(10), hand.getY() - scale(162 + 10));
			}
			else
			{
				ui.setLocation(multiButton.getX() + multiButton.getWidth() + scale(10), topbar.getHeight() + scale(10) + ((padding + scale(162)) * loc));
			}
			ui.setSize(getWidth() - scale(205), scale(162));
			//ui.setLocation(200, topbar.getHeight() + scale(10) + (loc * (MDPlayer.PLAYER_SIZE.height + 5)));
			player.setUIPosition(loc);
		}
		
		/*
		if (this instanceof ThePlayer)
		{
			ui.setLocation(200, 45 + (3 * (MDPlayer.PLAYER_SIZE.height + 5)));
			uiPos = 3;
		}
		else
		{
			ui.setLocation(200, 45 + (nextUIPos * (MDPlayer.PLAYER_SIZE.height + 5)));
			uiPos = nextUIPos++;
		}
		*/
	}
	
	public ActionScreen getActionScreen()
	{
		return actionScreen;
	}
	
	public void setActionScreen(ActionScreen screen)
	{
		removeActionScreen();
		actionScreen = screen;
		actionScreen.setSize(getSize());
		add(screen, new Integer(110));
	}
	
	public void removeActionScreen()
	{
		if (actionScreen != null)
		{
			remove(actionScreen);
			actionScreen = null;
			repaint();
		}
	}
	
	public MDChat getChat()
	{
		return chat;
	}
	
	public MDTopbar getTopbar()
	{
		return topbar;
	}
	
	public void setDeck(Deck deck)
	{
		/*
		if (this.deck != null)
		{
			remove(deck);
		}
		this.deck = deck;
		deck.setLocation(30, 60 - 10);
		deck.setSize(120 + 40, 180 + 24);
		add(deck, new Integer(0));
		*/
		deck.setUI(this.deck);
	}
	
	public MDDeck getDeck()
	{
		return deck;
	}
	
	public void setDiscardPile(DiscardPile discard)
	{
		/*
		if (this.discard != null)
		{
			remove(this.discard);
		}
		this.discard = discard;
		discard.setLocation(30, 260);
		discard.setSize(120 + 40, 180);
		add(discard, new Integer(0));
		*/
		discard.setUI(this.discard);
	}
	
	public MDDiscardPile getDiscardPile()
	{
		return discard;
	}
	
	public void setHand(Hand hand)
	{
		/*
		if (this.hand != null)
		{
			remove(this.hand);
		}
		this.hand = hand;
		hand.setLocation(200, 715);
		hand.setSize(1400 - 5, 180);
		add(hand, new Integer(0));
		*/
		hand.setUI(this.hand);
	}
	
	public MDHand getHand()
	{
		return hand;
	}
	
	public void setVoidCollection(VoidCollection voidCollection)
	{
		voidCollection.setUI(this.voidCollection);
	}
	
	public MDVoidCollection getVoidCollection()
	{
		return voidCollection;
	}
	
	public MDButton getMultiButton()
	{
		return multiButton;
	}
	
	public MDUndoButton getUndoButton()
	{
		return undoButton;
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
	
	private int scale(int size)
	{
		return GraphicsUtils.scale(size);
	}
	
	private int getMaxX(JComponent comp)
	{
		return comp.getX() + comp.getWidth();
	}
	
	private int getMaxY(JComponent comp)
	{
		return comp.getY() + comp.getHeight();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	public class TableLayout implements LayoutManager2
	{
		@Override
		public void addLayoutComponent(String arg0, Component arg1)
		{
			System.out.println("component added in other method");
		}
		
		@Override
		public void layoutContainer(Container container)
		{
			topbar.setSize(getWidth(), scale(35));
			
			debug.setLocation(getWidth() - scale(45), scale(5));
			debug.setSize(scale(40), scale(25));
			debug.setFontSize(16);
			
			enlargeUI.setLocation(scale(5), scale(5));
			enlargeUI.setSize(scale(25), scale(25));
			
			uiScale.setLocation(enlargeUI.getMaxX() + scale(6), scale(5));
			uiScale.setSize(scale(30), scale(30));
			
			shrinkUI.setLocation(uiScale.getMaxX() + scale(5), scale(5));
			shrinkUI.setSize(scale(25), scale(25));
			
			chat.setSize(scale(650), scale(450));
			chat.setLocation(scale(50), getHeight() - scale(650));
			
			version.setLocation(scale(5), getHeight() - scale(20));
			version.setSize(scale(200), scale(15));
			
			if (deck != null && discard != null)
			{
				deck.setLocation(scale(30), getMaxY(topbar) + scale(15));
				deck.setSize(scale(120 + 40), scale(180 + 24));
				
				discard.setLocation(scale(30), getMaxY(deck) + scale(6));
				discard.setSize(discard.getPreferredSize());
				
				voidCollection.setLocation(scale(40), discard.getMaxY());
				voidCollection.setSize(scale(60 + 40), scale(90 + 40));
				
				turnCount.setLocation(scale(10), getMaxY(discard) + scale(20));
				turnCount.setSize(scale(180) + 1, scale(30));
				
				multiButton.setLocation(scale(10), getMaxY(turnCount) + scale(10));
				multiButton.setSize(scale(180), scale(50));
				multiButton.setFontSize(24);
				
				undoButton.setLocation(scale(10), getMaxY(multiButton) + scale(20));
				undoButton.setSize(scale(180), scale(50));
				undoButton.setFontSize(24);
				
				hand.setLocation(getMaxX(undoButton) + scale(10), getHeight() - scale(185));
				hand.setSize(getWidth() - multiButton.getWidth() - scale(25), scale(180));
				
				positionPlayers();
			}
			
			if (actionScreen != null)
			{
				actionScreen.setSize(getSize());
			}
		}
		
		@Override
		public Dimension minimumLayoutSize(Container arg0)
		{
			System.out.println("minimum layout " + arg0);
			return null;
		}
		
		@Override
		public Dimension preferredLayoutSize(Container arg0)
		{
			System.out.println("preferred layout " + arg0);
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void removeLayoutComponent(Component arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void addLayoutComponent(Component comp, Object arg1)
		{
			// TODO Auto-generated method stub
			System.out.println("component added " + arg1);
		}
		
		@Override
		public float getLayoutAlignmentX(Container arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public float getLayoutAlignmentY(Container arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public void invalidateLayout(Container arg0)
		{
			layoutContainer(arg0);
		}
		
		@Override
		public Dimension maximumLayoutSize(Container arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
