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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oldmana.md.client.MDClient;
import oldmana.md.client.Player;
import oldmana.md.client.card.collection.PropertySet;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDButton;
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
import oldmana.md.common.playerui.ButtonColorScheme;
import oldmana.md.common.playerui.InfoPlateBase;

public class MDPlayer extends MDComponent
{
	public static final int PLAYER_SIZE = 145;
	
	private Player player;
	
	private MDInvisibleHand hand;
	private MDBank bank;
	
	private List<MDClientButton> buttons = new ArrayList<MDClientButton>();
	
	private List<InfoPlate> infoPlates = new ArrayList<InfoPlate>();
	
	private MDPlayerPropertySets propertySets;
	private MDButton setsScrollBack;
	private MDButton setsScrollForward;
	
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
		
		setsScrollBack = new MDButton("<");
		setsScrollBack.setColor(ButtonColorScheme.GRAY);
		setsScrollBack.addClickListener(() ->
		{
			propertySets.setScrollPos(Math.max(propertySets.getScrollPos() - propertySets.getInterval(), 0));
			propertySets.invalidate();
			updatePropertySetScrollButtons();
		});
		setsScrollBack.setVisible(false);
		add(setsScrollBack);
		setsScrollForward = new MDButton(">");
		setsScrollForward.setColor(ButtonColorScheme.GRAY);
		setsScrollForward.addClickListener(() ->
		{
			propertySets.setScrollPos(Math.min(propertySets.getScrollPos() + propertySets.getInterval(), propertySets.getScrollMax()));
			propertySets.invalidate();
			updatePropertySetScrollButtons();
		});
		setsScrollForward.setVisible(false);
		add(setsScrollForward);
	}
	
	public Player getPlayer()
	{
		return player;
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
	
	public InfoPlate getAndCreateInfoPlate(int id)
	{
		InfoPlate plate = getInfoPlate(id);
		if (plate != null)
		{
			return plate;
		}
		plate = new InfoPlate();
		plate.setId(id);
		infoPlates.add(plate);
		invalidate();
		return plate;
	}
	
	public InfoPlate getInfoPlate(int id)
	{
		for (InfoPlate plate : infoPlates)
		{
			if (plate.getId() == id)
			{
				return plate;
			}
		}
		return null;
	}
	
	public void removeInfoPlate(int id)
	{
		InfoPlateBase plate = getInfoPlate(id);
		infoPlates.remove(plate);
		invalidate();
	}
	
	public void sortInfoPlates()
	{
		infoPlates.sort(Comparator.comparingInt(InfoPlateBase::getPriority).reversed());
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
	
	public void updatePropertySetScrollButtons()
	{
		setsScrollBack.setEnabled(propertySets.getScrollPos() > 0);
		setsScrollForward.setEnabled(propertySets.getScrollPos() < propertySets.getScrollMax());
	}
	
	private boolean arePlatesExtendingIntoProperties(int setsX)
	{
		Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
		FontMetrics metrics = GraphicsUtils.getFontMetrics(font);
		int plateInnerPadding = scale(8);
		int nameWidth = metrics.stringWidth(getNameText());
		final int platePadding = scale(12);
		int xPos = nameWidth + plateInnerPadding + platePadding;
		for (InfoPlateBase plate : infoPlates)
		{
			int plateWidth = metrics.stringWidth(plate.getText());
			xPos += plateWidth + plateInnerPadding + platePadding;
		}
		return xPos > setsX;
	}
	
	private String getNameText()
	{
		return MDClient.getInstance().isDebugEnabled() ? player.getName() + " (ID: " + player.getID() + ")" : player.getName();
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color border = getBorderColor();
		Color nameplate = getNameplateColor();
		Color inner = getInnerColor();
		g.setColor(inner);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, scale(10), scale(10));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		String nameText = getNameText();
		int plateInnerPadding = scale(8);
		int nameWidth = metrics.stringWidth(nameText);
		int plateHeight = font.getSize() + 1;
		g.setColor(nameplate);
		GradientPaint paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(nameplate, 1), 0, (float) (plateHeight * 0.6), nameplate);
		g.setPaint(paint);
		
		g.fillRoundRect(0, 0, nameWidth + plateInnerPadding, plateHeight + scale(2), scale(10), scale(10));
		g.setColor(player.isConnected() ? Color.BLACK : Color.RED);
		TextPainter tp = new TextPainter(nameText, font, new Rectangle(scale(4), scale(3), nameWidth, plateHeight));
		tp.paint(g);
		g.setColor(border);
		g.drawRoundRect(0, 0, nameWidth + plateInnerPadding, plateHeight + scale(2), scale(10), scale(10));
		
		final int platePadding = scale(12);
		int xPos = nameWidth + plateInnerPadding + platePadding;
		
		for (InfoPlateBase plate : infoPlates)
		{
			String text = plate.getText();
			Color textColor = plate.getTextColor() != null ? plate.getTextColor() : Color.BLACK;
			Color plateColor = plate.getColor() != null ? plate.getColor() : nameplate;
			Color plateBorderColor = plate.getBorderColor() != null ? plate.getBorderColor() : border;
			int plateWidth = metrics.stringWidth(text);
			paint = new GradientPaint(0, 0, GraphicsUtils.getLighterColor(plateColor, 1), 0, (float) (plateHeight * 0.6), plateColor);
			g.setPaint(paint);
			g.fillRoundRect(xPos, 0, plateWidth + plateInnerPadding, plateHeight + scale(2), scale(10), scale(10));
			g.setColor(textColor);
			tp = new TextPainter(text, font, new Rectangle(xPos + scale(4), scale(3), plateWidth, plateHeight));
			tp.paint(g);
			g.setColor(plateBorderColor);
			g.drawRoundRect(xPos, 0, plateWidth + plateInnerPadding, plateHeight + scale(2), scale(10), scale(10));
			
			xPos += plateWidth + plateInnerPadding + platePadding;
		}
	}
	
	public class PlayerLayout extends LayoutAdapter
	{
		private boolean layingOut = false;
		
		@Override
		public void layoutContainer(Container container)
		{
			if (layingOut)
			{
				return;
			}
			layingOut = true;
			boolean hasButtons = !buttons.isEmpty();
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
			
			int infoPlateSpace = scale(20);
			int setsHeight = getHeight() - scale(10);
			int setsY = scale(5);
			
			int x = bank == null ? scale(10) : (bank.getX() + bank.getWidth() + scale(20));
			
			int width = getWidth() - x - scale(5);
			
			int scrollButtonWidth = scale(20);
			
			int prevScroll = propertySets.getScrollPos();
			propertySets.setLocation(x, setsY);
			propertySets.setSize(width, setsHeight);
			
			boolean scrollRequired = propertySets.checkScrollRequired();
			
			if (setsScrollBack == null || setsScrollForward == null)
			{
				layingOut = false;
				return;
			}
			setsScrollBack.setVisible(scrollRequired);
			setsScrollForward.setVisible(scrollRequired);
			if (scrollRequired)
			{
				setsScrollBack.setSize(scrollButtonWidth, setsHeight);
			}
			
			if (arePlatesExtendingIntoProperties(x))
			{
				setsY += infoPlateSpace;
				setsHeight -= infoPlateSpace;
			}
			
			if (scrollRequired)
			{
				x += setsScrollBack.getWidth();
				width -= scrollButtonWidth * 2;
			}
			
			propertySets.setLocation(x, setsY);
			propertySets.setSize(width, setsHeight);
			propertySets.checkScrollRequired();
			propertySets.setScrollPos(Math.min(prevScroll, propertySets.getScrollMax()));
			propertySets.invalidate();
			if (scrollRequired)
			{
				setsScrollBack.setLocation(x - setsScrollBack.getWidth(), setsY);
				setsScrollBack.setSize(scrollButtonWidth, setsHeight);
				setsScrollForward.setLocation(Math.max(propertySets.getMaxX(), setsScrollBack.getMaxX() + scale(5)), setsY);
				setsScrollForward.setSize(scrollButtonWidth, setsHeight);
			}
			
			updatePropertySetScrollButtons();
			
			layingOut = false;
		}
		
		@Override
		public void invalidateLayout(Container c)
		{
			layoutContainer(c);
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
	
	
	public class InfoPlate extends InfoPlateBase
	{
		@Override
		public void setText(String text)
		{
			super.setText(text);
			invalidate();
		}
	}
}
