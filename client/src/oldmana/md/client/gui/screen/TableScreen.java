package oldmana.md.client.gui.screen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager2;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardRegistry;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.gui.action.ActionScreen;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDChat;
import oldmana.md.client.gui.component.MDDeck;
import oldmana.md.client.gui.component.MDDiscardPile;
import oldmana.md.client.gui.component.MDTurns;
import oldmana.md.client.gui.component.MDUndoButton;
import oldmana.md.client.gui.component.large.MDHand;
import oldmana.md.client.gui.component.large.MDPlayer;
import oldmana.md.client.gui.component.large.MDTopbar;
import oldmana.md.client.gui.util.GraphicsUtils;

public class TableScreen extends JLayeredPane
{
	private MDTopbar topbar;
	private MDDeck deck;
	private MDDiscardPile discard;
	private MDHand hand;
	
	private MDButton multiButton;
	private MDUndoButton undoButton;
	private MDTurns turnCount;
	
	private MDChat chat;
	
	private ActionScreen actionScreen;
	
	private MDButton debug;
	
	private JLabel version;
	
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
		
		topbar = new MDTopbar();
		topbar.setText("");
		topbar.setSize(1600, 35);
		add(topbar, new Integer(0));
		undoButton = new MDUndoButton("Undo Card");
		undoButton.setLocation(10, 570);
		undoButton.setSize(180, 50);
		undoButton.setFontSize(24);
		add(undoButton, new Integer(0));
		
		multiButton = new MDButton("");
		multiButton.setLocation(10, 500);
		multiButton.setSize(180, 50);
		multiButton.setFontSize(24);
		multiButton.setEnabled(false);
		add(multiButton, new Integer(0));
		
		turnCount = new MDTurns();
		turnCount.setSize(181, 30);
		turnCount.setLocation(10, 460);
		add(turnCount, new Integer(0));
		
		version = new JLabel("Version " + MDClient.VERSION);
		version.setLocation(5, 880);
		version.setSize(200, 15);
		version.setFont(GraphicsUtils.getThinMDFont(Font.PLAIN, 16));
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
		
		//add(glass, new Integer(1000));
		
		chat = new MDChat();
		chat.setSize(500, 400);
		chat.setLocation(50, 300);
		add(chat, new Integer(150));
		/*
		MDClient.getInstance().getScheduler().scheduleTask(new MDTask(100, true)
		{
			@Override
			public void run()
			{
				chat.addMessage("Back in alpha, we had dirt and it worked. Because you know, alpha dirt is just that great.");
				//chat.requestFocus();
				//chat.requestFocusInWindow();
			}
		});
		*/
		
		
		//MDCard card = new MDCard(new CardProperty(500, PropertyColor.GREEN, 5, "A Property"), 3);
		//MDCard card = new MDCard(new CardActionRent(500, 3, PropertyColor.values()), 1.5);
		//card.setLocation(100, 100);
		//add(card, new Integer(200));
		this.requestFocusInWindow();
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
		System.out.println("Space: " + space);
		System.out.println("Padding: " + padding);
		
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
		actionScreen = screen;
		add(screen, new Integer(110));
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
	
	public MDButton getMultiButton()
	{
		return multiButton;
	}
	
	public MDUndoButton getUndoButton()
	{
		return undoButton;
	}
	
	public void test()
	{
		//setLayout(null);
		
		//MDMovingCard mc = new MDMovingCard(null, new CardMoney(501, 10), new Point(50, 80), new Point(300, 700));
		//add(mc);
		
		/*
		Timer t = new Timer(1000, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				deck.addUnknownCard();
				deck.getUI().repaint();
			}
			
		});
		t.setRepeats(true);
		t.start();
		*/
		//deck.addUnknownCard();
		
		//discard.addCard(new CardProperty(2, PropertyColor.BROWN, 3, "Brown Card"));
		
		/*
		MDSelection sel = new MDSelection();
		sel.setSize(400, 400);
		add(sel, 3);
		MDCreateSet cs = new MDCreateSet();
		add(cs, new Integer(1));
		*/
		
		/*
		PropertySet set = new PropertySet(1, null, new CardProperty(1, PropertyColor.BROWN, 1, "Brown Card"), PropertyColor.BROWN);
		set.addCard(new CardProperty(2, PropertyColor.BROWN, 3, "Brown Card"));
		set.addCard(new CardProperty(2, PropertyColor.BROWN, 4, "Brown Card"));
		set.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		PropertySet set2 = new PropertySet(1, null, new CardProperty(1, PropertyColor.ORANGE, 1, "Brown Card"), PropertyColor.BROWN);
		set2.addCard(new CardProperty(2, PropertyColor.ORANGE, 3, "Brown Card"));
		set2.addCard(new CardProperty(2, PropertyColor.ORANGE, 4, "Brown Card"));
		set2.addCard(new CardProperty(2, Arrays.asList(PropertyColor.values()), true, 4, "Property Wild Card"));*/
		//add(set.getUI(), 0);
		
		/*
		Player player = new ThePlayer(MDClient.getInstance(), 0, "Oldmana");
		add(player.getUI());
		player.addPropertySet(set);
		player.addPropertySet(set2);*/
		/*
		Player player2 = new Player(MDClient.getInstance(), 1, "Zyga - The Ruler Of The 20 MDs");
		add(player2.getUI());
		Player player3 = new Player(MDClient.getInstance(), 2, "[Aether]<mine_diver>");
		add(player3.getUI());
		Player player4 = new Player(MDClient.getInstance(), 3, "AndrewKart");
		add(player4.getUI());
		*/
		/*
		Hand hand = new Hand(0, player);
		hand.addCard(new CardProperty(2, PropertyColor.BROWN, 10, "Mediterranean Avenue"));
		hand.addCard(new CardProperty(2, PropertyColor.DARK_BLUE, 5, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 0, "Brown Card"));
		//hand.addCard(new CardProperty(2, PropertyColor.BROWN, 5, "Brown Card"));
		hand.addCard(new CardAction(2, 4, "JUST SAY NO!"));
		hand.addCard(new CardAction(2, 3, "SLY DEAL"));
		hand.addCard(new CardMoney(2, 5));
		hand.addCard(new CardMoney(2, 10));
		hand.addCard(new CardProperty(2, PropertyColor.RAILROAD, 0, "Railroad"));
		hand.addCard(new CardProperty(2, Arrays.asList(PropertyColor.values()), true, 0, "Property Wild Card"));
		this.hand = (MDHand) hand.getUI();
		add(hand.getUI());
		MouseAdapter listener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				System.out.println(event.getComponent());
			}
		};
		hand.getUI().addMouseListener(listener);
		hand.getUI().getMDCards().get(0).addMouseListener(listener);
		
		Timer t = new Timer(1000, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				//player.getHand().addUnknownCard();
				//player.getBank().addCard(new Card(2, 1, "A Card"));
			}
			
		});
		t.setRepeats(true);
		t.start();
		*/
		/*
		for (int i = 0 ; i < 7 ; i++)
		{
			player.getHand().addUnknownCard();
		}
		for (int i = 0 ; i < 3 ; i++)
		{
			player2.getHand().addUnknownCard();
		}
		for (int i = 0 ; i < 20 ; i++)
		{
			player.getBank().addCard(new CardAction(2, 4, "JUST SAY NO!"));
		}
		*/
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
			System.out.println("layout");
			
			topbar.setSize(getWidth(), scale(35));
			
			debug.setLocation(getWidth() - scale(45), scale(5));
			debug.setSize(scale(40), scale(25));
			debug.setFontSize(16);
			
			version.setLocation(scale(5), getHeight() - scale(20));
			version.setSize(scale(200), scale(15));
			
			if (deck != null && discard != null)
			{
				deck.setLocation(scale(30), getMaxY(topbar) + scale(15));
				deck.setSize(scale(120 + 40), scale(180 + 24));
				
				discard.setLocation(scale(30), getMaxY(deck) + scale(6));
				discard.setSize(scale(120 + 40), scale(180));
				
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
