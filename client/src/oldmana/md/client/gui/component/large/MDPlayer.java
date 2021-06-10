package oldmana.md.client.gui.component.large;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.ThePlayer;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDBank;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDInvisibleHand;
import oldmana.md.client.gui.component.MDPlayerPropertySets;
import oldmana.md.client.gui.component.MDPropertySet;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.GameState;

public class MDPlayer extends MDComponent
{
	private Player player;
	
	private MDInvisibleHand hand;
	private MDBank bank;
	
	private MDPlayerPropertySets propertySets;
	
	public MDPlayer(Player player)
	{
		this.player = player;
		setLayout(new PlayerLayout());
		propertySets = new MDPlayerPropertySets(player);
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
		ActionState state = gs.getActionState();
		Color border = null;
		Color nameplate = null;
		if (gs.getWhoseTurn() == player)
		{
			border = GraphicsUtils.DARK_BLUE;
			nameplate = GraphicsUtils.LIGHT_BLUE;
		}
		else if (state != null)
		{
			if (state.isTarget(player))
			{
				if (state.isAccepted(player))
				{
					border = GraphicsUtils.GREEN;
					nameplate = border.brighter();
				}
				else if (state.isRefused(player))
				{
					border = Color.BLUE;
					nameplate = new Color(80, 80, 255);
				}
				else
				{
					border = GraphicsUtils.RED;
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
		String nameText = MDClient.getInstance().isDebugEnabled() ? player.getName() + " (ID: " + player.getID() + ")" : player.getName();
		int nameWidth = metrics.stringWidth(nameText);
		int nameHeight = metrics.getHeight();
		g.setColor(nameplate);
		GradientPaint paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(nameplate, 1), 0, (float) (nameHeight * 0.6), nameplate);
		g.setPaint(paint);
		g.fillRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		if (!player.isConnected())
		{
			g.setColor(Color.RED);
		}
		else
		{
			g.setColor(Color.BLACK);
		}
		TextPainter tp = new TextPainter(nameText, font, new Rectangle(scale(4), scale(2), nameWidth, nameHeight));
		tp.paint(g);
		g.setColor(border);
		g.drawRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		//g.setFont(new Font(this.getFont().getFontName(), Font.ITALIC, 16));
		//g.drawString(player.getName(), 2, 16);
	}
	
	public class PlayerLayout extends LayoutAdapter
	{
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
		public void addLayoutComponent(Component component, Object arg1)
		{
			layoutContainer(MDPlayer.this);
		}
	}
}
