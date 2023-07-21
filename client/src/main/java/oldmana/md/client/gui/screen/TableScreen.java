package oldmana.md.client.gui.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.RepaintManager;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.card.collection.VoidCollection;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.action.ActionScreen;
import oldmana.md.client.gui.component.MDButton;
import oldmana.md.client.gui.component.MDChat;
import oldmana.md.client.gui.component.MDLayeredButton;
import oldmana.md.client.gui.component.MDLeftGradient;
import oldmana.md.client.gui.component.MDText;
import oldmana.md.client.gui.component.MDMoves;
import oldmana.md.client.gui.component.MDUndoButton;
import oldmana.md.client.gui.component.collection.MDDeck;
import oldmana.md.client.gui.component.collection.MDDiscardPile;
import oldmana.md.client.gui.component.collection.MDHand;
import oldmana.md.client.gui.component.collection.MDVoidCollection;
import oldmana.md.client.gui.component.large.MDPlayer;
import oldmana.md.client.gui.component.large.MDOpponents;
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
	
	private MDLeftGradient leftGradient;
	
	private MDPlayer self;
	private MDOpponents opponents;
	
	private MDButton multiButton;
	private MDUndoButton undoButton;
	private MDMoves moveCount;
	
	private MDChat chat;
	
	private ActionScreen actionScreen;
	
	private MDText version;
	
	private MDButton menu;
	
	private MDButton debug;
	private MDButton enlargeUI;
	private MDButton shrinkUI;
	private MDText uiScale;
	
	public IngameMenuScreen ingameMenu;
	
	public TableScreen()
	{
		super();
		setOpaque(true);
		setBackground(new Color(240, 240, 240));
		System.out.println("Opaque: " + isOpaque());
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
		
		leftGradient = new MDLeftGradient();
		add(leftGradient, new Integer(-1));
		
		opponents = new MDOpponents();
		add(opponents);
		
		topbar = new MDTopbar();
		topbar.setText("");
		add(topbar, new Integer(0));
		
		undoButton = new MDUndoButton("Undo Card");
		undoButton.setFontSize(24);
		add(undoButton, new Integer(0));
		
		multiButton = new MDLayeredButton("");
		multiButton.setFontSize(24);
		multiButton.setEnabled(false);
		add(multiButton, new Integer(0));
		
		moveCount = new MDMoves();
		add(moveCount, new Integer(0));
		
		version = new MDText("Version " + MDClient.VERSION);
		version.setFontSize(16);
		add(version);
		
		chat = new MDChat();
		add(chat, new Integer(150));
		
		if (getClient().getSettings().getBoolean("extraButtons"))
		{
			debug = new MDButton("DB");
			debug.setFontSize(16);
			debug.addClickListener(() ->
			{
				getClient().setDebugEnabled(!getClient().isDebugEnabled());
				for (Card card : Card.getRegisteredCards().values())
				{
					card.clearGraphicsCache();
				}
				repaint();
			});
			add(debug, new Integer(1));
			
			enlargeUI = new MDButton("+");
			enlargeUI.addClickListener(() ->
			{
				GraphicsUtils.setScale(Math.min(GraphicsUtils.SCALE + 0.1, 4.0));
				uiScale.setText("" + GraphicsUtils.SCALE);
				revalidate();
				repaint();
				
				getClient().getGameState().updateUI();
			});
			add(enlargeUI, new Integer(1));
			
			shrinkUI = new MDButton("-");
			shrinkUI.addClickListener(() ->
			{
				GraphicsUtils.setScale(Math.max(GraphicsUtils.SCALE - 0.1, 0.5));
				uiScale.setText("" + GraphicsUtils.SCALE);
				revalidate();
				repaint();
				
				getClient().getGameState().updateUI();
			});
			add(shrinkUI, new Integer(1));
			
			uiScale = new MDText("" + GraphicsUtils.SCALE);
			uiScale.setFontSize(20);
			uiScale.setHorizontalAlignment(Alignment.CENTER);
			add(uiScale, new Integer(1));
		}
		else
		{
			menu = new MDButton("Menu");
			menu.addClickListener(() ->
			{
				if (chat.isChatOpen())
				{
					chat.setChatOpen(false);
				}
				ingameMenu.setVisible(true);
				ingameMenu.requestFocus();
			});
			add(menu, new Integer(1));
		}
		
		ingameMenu = new IngameMenuScreen();
		add(ingameMenu, new Integer(10000));
		System.out.println("Global double buffered: " +
		RepaintManager.currentManager(this).isDoubleBufferingEnabled());
		setDoubleBuffered(true);
		System.out.println("Double buffered: " + isDoubleBuffered());
	}
	
	public void addPlayer(Player player)
	{
		if (player instanceof ThePlayer)
		{
			if (self != null)
			{
				removePlayer(player);
			}
			self = player.getUI();
			add(self);
			return;
		}
		opponents.add(player.getUI());
		opponents.invalidate();
		opponents.repaint();
	}
	
	public void removePlayer(Player player)
	{
		if (player instanceof ThePlayer)
		{
			remove(self);
			self = null;
			return;
		}
		opponents.remove(player.getUI());
		opponents.invalidate();
		opponents.updateGraphics();
	}
	
	public MDOpponents getOpponents()
	{
		return opponents;
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
		deck.setUI(this.deck);
	}
	
	public MDDeck getDeck()
	{
		return deck;
	}
	
	public void setDiscardPile(DiscardPile discard)
	{
		discard.setUI(this.discard);
	}
	
	public MDDiscardPile getDiscardPile()
	{
		return discard;
	}
	
	public void setHand(Hand hand)
	{
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
	
	public MDMoves getMoves()
	{
		return moveCount;
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
	
	public int getPadding()
	{
		return scale(5);
	}
	
	public int getPadding(int multiplier)
	{
		return getPadding() * multiplier;
	}
	
	private int getSidebarSize()
	{
		return scale(200);
	}
	
	public class TableLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			topbar.setSize(getWidth(), scale(35));
			
			int sidebarSize = getSidebarSize();
			
			if (debug != null)
			{
				debug.setLocation(getWidth() - scale(45), scale(5));
				debug.setSize(scale(40), scale(25));
				debug.setFontSize(16);
				
				enlargeUI.setLocation(scale(5), scale(5));
				enlargeUI.setSize(scale(25), scale(25));
				
				uiScale.setLocation(enlargeUI.getMaxX() + scale(6), scale(5));
				uiScale.setSize(scale(30), scale(30));
				
				shrinkUI.setLocation(uiScale.getMaxX() + scale(5), scale(5));
				shrinkUI.setSize(scale(25), scale(25));
			}
			else
			{
				menu.setLocation(scale(5), scale(5));
				menu.setSize(scale(60), scale(25));
				menu.setFontSize(16);
			}
			
			chat.setSize(scale(750), scale(550));
			int chatX = scale(80);
			if (getHeight() > chat.getHeight())
			{
				chat.setLocationCenterY(chatX, getHeight() / 2);
			}
			else
			{
				chat.setLocation(chatX, getHeight() - chat.getHeight());
			}
			
			version.setLocation(scale(5), getHeight() - scale(20));
			version.setSize(sidebarSize, scale(15));
			
			if (deck != null && discard != null)
			{
				deck.setLocation(scale(30), getMaxY(topbar) + scale(15));
				deck.setSize(scale(120 + 40), scale(180 + 24));
				
				discard.setLocation(scale(30), getMaxY(deck) + scale(6));
				discard.setSize(discard.getPreferredSize());
				
				voidCollection.setLocation(scale(40), discard.getMaxY());
				voidCollection.setSize(scale(60 + 40), scale(90 + 40));
				
				moveCount.setSize(sidebarSize - getPadding(4), scale(30));
				moveCount.setLocationCenterX(sidebarSize / 2, getMaxY(discard) + getPadding(2));
				
				multiButton.setSize(sidebarSize - getPadding(4), scale(50));
				multiButton.setLocationCenterX(sidebarSize / 2, getMaxY(moveCount) + getPadding(2));
				multiButton.setFontSize(24);
				
				undoButton.setSize(sidebarSize - getPadding(4), scale(50));
				undoButton.setLocationCenterX(sidebarSize / 2, getMaxY(multiButton) + getPadding(2));
				undoButton.setFontSize(24);
				
				hand.setLocation(sidebarSize + getPadding(2), getHeight() - scale(185));
				hand.setSize(getWidth() - sidebarSize - getPadding(3), scale(180));
				
				if (self != null)
				{
					self.setSize(getWidth() - sidebarSize - getPadding(3), MDPlayer.getPlayerSize());
					self.setLocation(sidebarSize + getPadding(2), hand.getY() - getPadding() - self.getHeight());
				}
				int selfHeight = self != null ? self.getHeight() : 0;
				opponents.setLocation(sidebarSize + getPadding(2), topbar.getMaxY() + getPadding());
				opponents.setSize(getWidth() - sidebarSize - getPadding(3), getHeight() - (getHeight() - hand.getY()) -
						topbar.getHeight() - selfHeight - getPadding(3));
				
				leftGradient.setSize(deck.getMaxX() + getPadding(2), getHeight());
			}
			
			if (actionScreen != null)
			{
				actionScreen.setSize(getSize());
			}
			
			ingameMenu.setSize(getSize());
		}
		
		@Override
		public void invalidateLayout(Container arg0)
		{
			layoutContainer(arg0);
		}
	}
}
