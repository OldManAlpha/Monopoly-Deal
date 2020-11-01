package oldmana.general.md.client.gui.component.large;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JLayeredPane;

import oldmana.general.md.client.MDClient;
import oldmana.general.md.client.Player;
import oldmana.general.md.client.ThePlayer;
import oldmana.general.md.client.card.CardProperty.PropertyColor;
import oldmana.general.md.client.card.collection.PropertySet;
import oldmana.general.md.client.gui.component.MDBank;
import oldmana.general.md.client.gui.component.MDComponent;
import oldmana.general.md.client.gui.component.MDInvisibleHand;
import oldmana.general.md.client.gui.component.MDPlayerPropertySets;
import oldmana.general.md.client.gui.component.MDPropertySet;
import oldmana.general.md.client.gui.util.GraphicsUtils;
import oldmana.general.md.client.gui.util.TextPainter;
import oldmana.general.md.client.state.ActionState;
import oldmana.general.md.client.state.GameState;

public class MDPlayer extends MDComponent
{
	public static Dimension PLAYER_SIZE = new Dimension(1400 - 5, MDPropertySet.PROPERTY_SET_SIZE.height + 10);
	
	private Player player;
	
	private MDInvisibleHand hand;
	private MDBank bank;
	
	private MDPlayerPropertySets propertySets;
	
	public MDPlayer(Player player)
	{
		this.player = player;
		setSize(PLAYER_SIZE);
		setLayout(new PlayerLayout());
		propertySets = new MDPlayerPropertySets(player);
		propertySets.setLocation(395, 5);
		propertySets.setSize(MDPlayerPropertySets.SETS_SIZE);
		for (PropertySet set : player.getPropertySets())
		{
			propertySets.addPropertySet((MDPropertySet) set.getUI());
		}
		add(propertySets, 0);
		
	}
	
	public void addPropertySet(PropertySet set)
	{
		propertySets.addPropertySet((MDPropertySet) set.getUI());
	}
	
	public void removePropertySet(PropertySet set)
	{
		propertySets.removePropertySet((MDPropertySet) set.getUI());
	}
	
	public MDPlayerPropertySets getPropertySets()
	{
		return propertySets;
	}
	
	public void setHand(MDInvisibleHand hand)
	{
		if (this.hand != null)
		{
			remove(this.hand);
		}
		this.hand = hand;
		hand.setLocation(20, 45);
		hand.setSize(160, 90);
		add(hand);
	}
	
	public void setBank(MDBank bank)
	{
		if (this.bank != null)
		{
			remove(this.bank);
		}
		this.bank = bank;
		if (player instanceof ThePlayer)
		{
			bank.setLocation(20, 35);
			bank.setSize(340 + 10, 90 + 30);
		}
		else
		{
			bank.setLocation(200, 35);
			bank.setSize(160 + 10, 90 + 30);
		}
		add(bank);
	}
	
	public MDClient getClient()
	{
		return MDClient.getInstance();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GameState gs = getClient().getGameState();
		ActionState state = gs.getCurrentActionState();
		Color border = null;
		Color nameplate = null;
		if (gs.getWhoseTurn() == player)
		{
			border = PropertyColor.DARK_BLUE.getColor();
			nameplate = PropertyColor.LIGHT_BLUE.getColor();
		}
		else if (state != null)
		{
			if (state.isTarget(player))
			{
				if (state.isAccepted(player))
				{
					border = PropertyColor.GREEN.getColor();
					nameplate = border.brighter();
				}
				else if (state.isRefused(player))
				{
					border = Color.BLUE;
					nameplate = new Color(0, 0, 150);
				}
				else
				{
					border = PropertyColor.RED.getColor();
					nameplate = new Color(255, 169, 112);
				}
			}
			else
			{
				border = Color.BLACK;
				nameplate = Color.LIGHT_GRAY;
			}
		}
		else
		{
			border = Color.BLACK;
			nameplate = Color.LIGHT_GRAY;
		}
		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//Font font = new Font(getFont().getFontName(), Font.ITALIC, 16);
		Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		int nameWidth = metrics.stringWidth(player.getName());
		int nameHeight = metrics.getHeight();
		g.setColor(nameplate);
		g.fillRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		if (!player.isConnected())
		{
			g.setColor(Color.RED);
		}
		else
		{
			g.setColor(Color.BLACK);
		}
		TextPainter tp = new TextPainter(player.getName(), font, new Rectangle(scale(4), scale(2), nameWidth, nameHeight));
		tp.paint(g);
		g.setColor(border);
		g.drawRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		//g.setFont(new Font(this.getFont().getFontName(), Font.ITALIC, 16));
		//g.drawString(player.getName(), 2, 16);
	}
	
	public class PlayerLayout implements LayoutManager2
	{

		@Override
		public void addLayoutComponent(String arg0, Component arg1)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void layoutContainer(Container container)
		{
			Point bankPos = new Point(scale(20), scale(35));
			Dimension bankSize = new Dimension(scale(340 + 10), scale(90 + 30));
			if (hand != null)
			{
				hand.setLocation(scale(20), scale(45));
				hand.setSize(scale(160), scale(90));
				bankPos = new Point(scale(200), scale(35));
				bankSize = new Dimension(scale(160 + 10), scale(90 + 30));
			}
			if (bank != null)
			{
				bank.setLocation(bankPos);
				bank.setSize(bankSize);
			}
			int x = bank == null ? scale(10) : (bank.getX() + bank.getWidth() + scale(20));
			propertySets.setLocation(x, scale(5));
			propertySets.setSize(getWidth() - x - scale(5), getHeight() - scale(10));
			propertySets.update();
		}

		@Override
		public Dimension minimumLayoutSize(Container arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Dimension preferredLayoutSize(Container arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeLayoutComponent(Component arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addLayoutComponent(Component component, Object arg1)
		{
			layoutContainer(MDPlayer.this);
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
