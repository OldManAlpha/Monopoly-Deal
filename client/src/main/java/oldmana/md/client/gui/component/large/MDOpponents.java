package oldmana.md.client.gui.component.large;

import oldmana.md.client.Player;
import oldmana.md.client.gui.AutoScrollable;
import oldmana.md.client.gui.LayoutAdapter;
import oldmana.md.client.gui.component.MDComponent;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MDOpponents extends MDComponent implements AutoScrollable
{
	private boolean scrollEnabled;
	private int scrollPos;
	private int scrollMax;
	
	private boolean holdingScrollbar;
	private int heldY;
	
	private boolean scrollLock;
	
	public MDOpponents()
	{
		setLayout(new PlayersLayout());
		
		addMouseWheelListener(event ->
		{
			if (scrollEnabled)
			{
				int amt = event.getUnitsToScroll() * scale(5);
				scrollPos = Math.max(0, Math.min(scrollMax, scrollPos + amt));
				invalidate();
				updateGraphics();
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				Rectangle bar = getScrollBarBounds();
				if (bar.contains(e.getPoint()))
				{
					holdingScrollbar = true;
					heldY = e.getY() - bar.y;
					updateGraphics();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				holdingScrollbar = false;
				updateGraphics();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				if (holdingScrollbar)
				{
					double held = heldY / (double) getHeight();
					double pos = e.getY() / (double) getHeight();
					scrollPos = (int) Math.max(0, Math.min(scrollMax, (pos - held) * getRequiredSpace()));
					revalidate();
					updateGraphics();
				}
			}
		});
	}
	
	private List<Player> getOrder()
	{
		return getClient().getOtherPlayersOrdered();
	}
	
	public int getMiddleSpace()
	{
		return getHeight() - getTopPlayer().getHeight();
	}
	
	public MDPlayer getTopPlayer()
	{
		return !getOrder().isEmpty() ? getOrder().get(0).getUI() : null;
	}
	
	public List<MDPlayer> getMiddlePlayers()
	{
		return !getOrder().isEmpty() ?
				getOrder().subList(1, getOrder().size()).stream().map(p -> p.getUI()).collect(Collectors.toList()) :
				Collections.emptyList();
	}
	
	public Rectangle getScrollBarBounds()
	{
		int requiredSpace = getRequiredSpace();
		// Draw scroll bar
		double barSize = (double) getHeight() / requiredSpace;
		double barPos = (double) ((scrollMax - scrollPos) + getHeight()) / requiredSpace;
		int barOffset = (int) (getHeight() - (getHeight() * barPos));
		return new Rectangle(getWidth() - scale(10) - 1, barOffset, scale(10), (int) (barSize * getHeight()));
	}
	
	public int getRequiredSpace()
	{
		int playerSize = MDPlayer.getPlayerSize();
		int playerCount = getClient().getOtherPlayers().size();
		int padding = scale(5);
		return ((playerSize + padding) * playerCount) - padding;
	}
	
	public void setScrollLocked(boolean scrollLock)
	{
		this.scrollLock = scrollLock;
	}
	
	@Override
	public int getScrollNeededToView(MDComponent component)
	{
		return getScrollNeededToView(((MDPlayer) component).getPlayer());
	}
	
	public int getScrollPos()
	{
		return scrollPos;
	}
	
	public void setScrollPos(int scrollPos)
	{
		this.scrollPos = scrollPos;
		invalidate();
		updateGraphics();
	}
	
	public int getScrollNeededToView(Player player)
	{
		Rectangle rec = getPlayerPositionsUnscrolled().get(player.getUI());
		return (int) Math.max(0, rec.getMaxY() - getHeight());
	}
	
	private Color[] scrollBarGradient = new Color[] {new Color(210, 210, 210), new Color(180, 180, 180)};
	private Color[] heldScrollBarGradient = new Color[] {new Color(180, 180, 180), new Color(150, 150, 150)};
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		
		if (scrollEnabled)
		{
			Graphics2D g = (Graphics2D) gr;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Rectangle bar = getScrollBarBounds();
			LinearGradientPaint paint = new LinearGradientPaint(bar.x, 0, bar.x + bar.width, 0,
					new float[] {0, 1}, holdingScrollbar ? heldScrollBarGradient : scrollBarGradient);
			g.setPaint(paint);
			g.fillRoundRect(bar.x, bar.y, bar.width, bar.height, scale(8), scale(8));
			g.setColor(Color.DARK_GRAY);
			g.drawRoundRect(bar.x, bar.y, bar.width, bar.height, scale(8), scale(8));
		}
	}
	
	private final Color[] topGradient = new Color[] {new Color(240, 240, 240), new Color(240, 240, 240, 0)};
	private final Color[] bottomGradient = new Color[] {new Color(240, 240, 240, 0), new Color(240, 240, 240)};
	
	@Override
	public void paintChildren(Graphics gr)
	{
		super.paintChildren(gr);
		if (scrollEnabled)
		{
			Graphics2D g = (Graphics2D) gr;
			int gradientHeight = scale(12);
			if (scrollPos != scrollMax)
			{
				LinearGradientPaint paint = new LinearGradientPaint(0, getHeight() - gradientHeight, 0, getHeight(),
						new float[] {0, 1}, bottomGradient);
				g.setPaint(paint);
				g.fillRect(0, getHeight() - gradientHeight, getWidth() - scale(10) - 1, gradientHeight);
			}
			if (scrollPos != 0)
			{
				LinearGradientPaint paint = new LinearGradientPaint(0, 0, 0, gradientHeight,
						new float[] {0, 1}, topGradient);
				g.setPaint(paint);
				g.fillRect(0, 0, getWidth() - scale(10) - 1, gradientHeight);
			}
		}
	}
	
	private int getPlayerY(int playerIndex)
	{
		int playerCount = getClient().getOtherPlayers().size() - 1;
		
		int height = getHeight() - MDPlayer.getPlayerSize();
		double cardsWidth = MDPlayer.getPlayerSize() * playerCount;
		double padding = (height - cardsWidth) / (double) (playerCount + 1);
		padding = Math.max(padding, 0);
		
		double start = padding - ((padding / playerCount) * playerIndex);
		
		double room = height - cardsWidth;
		double interval = room / playerCount;
		
		return (int) (start + ((MDPlayer.getPlayerSize() + interval) * playerIndex));
	}
	
	public Map<MDPlayer, Rectangle> getPlayerPositionsUnscrolled()
	{
		Map<MDPlayer, Rectangle> positions = new HashMap<MDPlayer, Rectangle>();
		
		MDPlayer top = getTopPlayer();
		if (top == null)
		{
			return positions;
		}
		int playerSize = MDPlayer.getPlayerSize();
		
		List<MDPlayer> middle = getMiddlePlayers();
		int space = getMiddleSpace();
		int padding = scale(5);
		int requiredSpace = (playerSize * middle.size()) + (padding * (middle.size()));
		if (space >= requiredSpace)
		{
			positions.put(top, new Rectangle(0, 0, getWidth(), playerSize));
			for (int pos = 0 ; pos < middle.size() ; pos++)
			{
				positions.put(middle.get(pos), new Rectangle(0, getPlayerY(pos) + playerSize, getWidth(), playerSize));
			}
		}
		else
		{
			List<MDPlayer> all = getOrder().stream().map(p -> p.getUI()).collect(Collectors.toList());
			for (int pos = 0 ; pos < all.size() ; pos++)
			{
				double yPos = (playerSize + padding) * (pos);
				positions.put(all.get(pos), new Rectangle(0, (int) yPos, getWidth() - scale(15), playerSize));
			}
		}
		return positions;
	}
	
	public class PlayersLayout extends LayoutAdapter
	{
		@Override
		public void layoutContainer(Container c)
		{
			MDPlayer top = getTopPlayer();
			if (top == null)
			{
				return;
			}
			
			int playerSize = MDPlayer.getPlayerSize();
			List<MDPlayer> middle = getMiddlePlayers();
			int padding = scale(5);
			int requiredSpace = (playerSize * middle.size()) + (padding * (middle.size() + 1));
			int space = getMiddleSpace() + padding;
			Map<MDPlayer, Rectangle> positions = getPlayerPositionsUnscrolled();
			if (space >= requiredSpace)
			{
				scrollEnabled = false;
				scrollPos = 0;
				
				positions.forEach((player, pos) ->
				{
					player.setSize(pos.getSize());
					player.setLocation(pos.getLocation());
				});
			}
			else
			{
				scrollEnabled = true;
				scrollMax = requiredSpace + playerSize - getHeight() - padding;
				if (scrollPos > scrollMax)
				{
					scrollPos = scrollMax;
				}
				positions.forEach((player, pos) ->
				{
					player.setSize(pos.getSize());
					player.setLocation(pos.x, pos.y - scrollPos);
				});
			}
			updateGraphics();
		}
		
		@Override
		public void invalidateLayout(Container c)
		{
			layoutContainer(c);
		}
	}
}
