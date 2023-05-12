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
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDComponent;
import oldmana.md.client.gui.component.MDClientButton;
import oldmana.md.client.gui.component.MDPlayerPropertySets;
import oldmana.md.client.gui.component.collection.MDBank;
import oldmana.md.client.gui.component.collection.MDInvisibleHand;
import oldmana.md.client.gui.component.collection.MDPropertySet;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.GameState;

public class MDPlayer extends MDComponent
{
	public static final int PLAYER_SIZE = 145;
	
	private Player player;
	
	private MDInvisibleHand hand;
	private MDBank bank;
	
	private List<MDClientButton> buttons = new ArrayList<MDClientButton>();
	
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
	
	public List<MDClientButton> getButtons()
	{
		return buttons;
	}
	
	public MDClientButton getAndCreateButton(Player view, int id)
	{
		MDClientButton button = getButton(id);
		if (button != null)
		{
			return button;
		}
		button = new MDClientButton(view, id);
		add(button);
		buttons.add(button);
		return button;
	}
	
	public MDClientButton getButton(int id)
	{
		for (MDClientButton button : buttons)
		{
			if (button.getID() == id)
			{
				return button;
			}
		}
		return null;
	}
	
	public boolean removeButton(int id)
	{
		MDClientButton button = getButton(id);
		if (button != null)
		{
			remove(button);
			buttons.remove(button);
			return true;
		}
		return false;
	}
	
	public void setHand(MDInvisibleHand hand)
	{
		if (this.hand != null)
		{
			remove(this.hand);
		}
		this.hand = hand;
		add(hand);
	}
	
	public void setBank(MDBank bank)
	{
		if (this.bank != null)
		{
			remove(this.bank);
		}
		this.bank = bank;
		add(bank);
	}
	
	private static Color[][] colors = new Color[][]
	{
		{GraphicsUtils.DARK_BLUE, GraphicsUtils.GREEN, GraphicsUtils.ORANGE, GraphicsUtils.RED, new Color(100, 100, 100)},
		{GraphicsUtils.LIGHT_BLUE, GraphicsUtils.GREEN.brighter(), new Color(240, 240, 100), new Color(255, 169, 112), Color.LIGHT_GRAY},
		{new Color(232, 237, 240), new Color(235, 240, 235), new Color(240, 240, 235), new Color(240, 235, 235), new Color(236, 236, 236)}
	};
	
	public Color getColor(int type)
	{
		GameState gs = getClient().getGameState();
		ActionState state = gs.getActionState();
		if (gs.getWhoseTurn() == player)
		{
			return colors[type][0];
		}
		if (state != null && state.isTarget(player))
		{
			if (state.isAccepted(player))
			{
				return colors[type][1];
			}
			if (state.isRefused(player))
			{
				return colors[type][2];
			}
			return colors[type][3];
		}
		return colors[type][4];
	}
	
	public Color getBorderColor()
	{
		return getColor(0);
	}
	
	public Color getNameplateColor()
	{
		return getColor(1);
	}
	
	public Color getInnerColor()
	{
		return getColor(2);
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color border = getBorderColor();
		Color nameplate = getNameplateColor();
		Color inner = getInnerColor();
		//Color innerDarker = GraphicsUtils.getDarkerColor(inner, 0.3);
		//LinearGradientPaint background = new LinearGradientPaint(0, 0, getWidth(), getHeight(), new float[] {0, 1}, new Color[] {inner, innerDarker});
		//g.setPaint(background);
		g.setColor(inner);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		String nameText = MDClient.getInstance().isDebugEnabled() ? player.getName() + " (ID: " + player.getID() + ")" : player.getName();
		int nameWidth = metrics.stringWidth(nameText);
		int nameHeight = font.getSize() + 1;
		g.setColor(nameplate);
		GradientPaint paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(nameplate, 1), 0, (float) (nameHeight * 0.6), nameplate);
		g.setPaint(paint);
		g.fillRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		g.setColor(player.isConnected() ? Color.BLACK : Color.RED);
		TextPainter tp = new TextPainter(nameText, font, new Rectangle(scale(4), scale(3), nameWidth, nameHeight));
		tp.paint(g);
		g.setColor(border);
		g.drawRoundRect(0, 0, nameWidth + scale(8), nameHeight + scale(2), scale(10), scale(10));
		
		
	}
	
	public class PlayerLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container container)
		{
			boolean hasButtons = buttons.size() > 0;
			Point bankPos = new Point(scale(20), scale(hasButtons ? 5 : 15));
			Dimension bankSize = new Dimension(scale(340 + 10), scale(90 + 20));
			if (hand != null)
			{
				hand.setLocation(scale(20), scale(20 + (hasButtons ? 5 : 15)));
				hand.setSize(scale(160), scale(90));
				bankPos = new Point(scale(200), scale(hasButtons ? 5 : 15));
				bankSize = new Dimension(scale(160 + 10), scale(90 + 20));
			}
			if (bank != null)
			{
				bank.setLocation(bankPos);
				bank.setSize(bankSize);
			}
			
			if (hasButtons)
			{
				double totalSize = 0;
				Map<Integer, List<MDClientButton>> orderedButtons = new TreeMap<Integer, List<MDClientButton>>();
				for (MDClientButton button : buttons)
				{
					totalSize += button.getMaxSize();
					orderedButtons.computeIfAbsent(button.getPriority(), key -> new ArrayList<MDClientButton>());
					orderedButtons.get(button.getPriority()).add(button);
				}
				
				int padding = scale(5);
				int space = scale(350) - ((buttons.size() - 1) * padding);
				int pos = 0;
				for (List<MDClientButton> priorityButtons : orderedButtons.values())
				{
					for (MDClientButton button : priorityButtons)
					{
						int buttonSize = (int) ((button.getMaxSize() * space) / Math.max(totalSize, 1));
						button.setSize(buttonSize, scale(25));
						button.setLocation(scale(20) + pos, scale(118));
						pos += buttonSize + padding;
					}
				}
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
	
	public static int getPlayerSize()
	{
		return GraphicsUtils.scale(PLAYER_SIZE);
	}
}
