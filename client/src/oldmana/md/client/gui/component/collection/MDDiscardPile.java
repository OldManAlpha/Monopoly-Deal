package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.DiscardPile;
import oldmana.md.client.gui.component.MDInfoIcon;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDDiscardPile extends MDCardCollection
{
	private MDInfoIcon icon;
	
	public int scrollPos;
	
	private int animTicks;
	private boolean animDir;
	private boolean collapsing;
	
	public MDDiscardPile(DiscardPile discard)
	{
		super(discard, 2);
		update();
		addMouseListener(new MDDiscardListener());
		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent event)
			{
				if (event.getUnitsToScroll() > 0)
				{
					if (scrollPos < getCurrentCardCount() - 1)
					{
						scrollPos++;
						if (scrollPos == 1)
						{
							getParent().invalidate();
						}
						animDir = true;
						animTicks = 15;
						constructInfoIcon();
						repaint();
					}
				}
				else if (event.getUnitsToScroll() < 0)
				{
					if (scrollPos > 0)
					{
						scrollPos--;
						if (scrollPos == 0)
						{
							getParent().invalidate();
						}
						animDir = false;
						animTicks = 15;
						constructInfoIcon();
						repaint();
					}
				}
			}
		});
		getClient().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				if (animTicks > 0)
				{
					animTicks--;
					repaint();
				}
				if (collapsing)
				{
					for (int i = 0 ; i < 4 ; i++)
					{
						if (animTicks == 0)
						{
							if (--scrollPos == 0)
							{
								getParent().invalidate();
								collapsing = false;
								break;
							}
							else
							{
								animTicks = 15;
							}
						}
						else
						{
							animTicks--;
						}
					}
				}
			}
		});
	}
	
	public void cardAdded()
	{
		if (scrollPos > 0)
		{
			scrollPos++;
		}
	}
	
	public void cardRemoved()
	{
		if (scrollPos > 0)
		{
			scrollPos--;
			if (scrollPos == 0)
			{
				getParent().invalidate();
				collapsing = false;
				animTicks = 0;
			}
		}
	}

	@Override
	public void update()
	{
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (getCollection() != null)
		{
			if (getCollection().getCardCount() - (isCardIncoming() ? 1 : 0) > 0)
			{
				g.setColor(Color.DARK_GRAY);
				g.fillRoundRect(scale(60), 0, scale(60) + (int) Math.floor(getMainPileCardCount() * (0.3 * GraphicsUtils.SCALE)), scale(180), scale(20), scale(20));
				g.drawImage(getMainPileFace()
						.getGraphics(getScale() * 2), 0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), null);
				if (scrollPos > 0)
				{
					if (getCurrentCardCount() > getCurrentCardCount() - scrollPos + (animTicks > 0 && animDir ? 1 : 0))
					{
						g.fillRoundRect(scale(60), scale(186), scale(60) + (int) Math.floor(getOtherPileCardCount() * (0.3 * GraphicsUtils.SCALE)), 
								scale(180), scale(20), scale(20));
						g.drawImage(getOtherPileFace().getGraphics(getScale() * 2), 0, scale(186), GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), 
								null);
					}
				}
				if (animTicks > 0)
				{
					double prog = (double) animTicks / 15;
					g.drawImage(getMovingCard().getGraphics(getScale() * 2), 0, scale(!animDir ? prog * 186 : 186 - (prog * 186)), GraphicsUtils.getCardWidth(2), 
							GraphicsUtils.getCardHeight(2), null);
				}
			}
			else
			{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRoundRect(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), scale(20), scale(20));
				g.setColor(Color.BLACK);
				Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
				g.setFont(font);
				TextPainter tp = new TextPainter("Discard Empty", font, new Rectangle(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			
			if (getClient().isDebugEnabled())
			{
				g.setColor(Color.ORANGE);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(30), GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight());
			}
		}
		//g.fillRect(60, 0, collection.getCardCount(), 90);
	}
	
	public void constructInfoIcon()
	{
		destroyInfoIcon();
		if (!getCollection().isEmpty())
		{
			icon = new MDInfoIcon(getCollection().getCardAt(getCollection().getCardCount() - 1 - scrollPos));
			icon.setLocation(GraphicsUtils.getCardWidth(2) - scale(24), scale(2));
			add(icon);
			repaint();
		}
	}
	
	public void destroyInfoIcon()
	{
		if (icon != null)
		{
			icon.removeCardInfo();
			remove(icon);
			icon = null;
		}
	}
	
	public Card getMainPileFace()
	{
		return getCollection().getCardAt(Math.min(getCardCount() - 1, getCurrentCardCount() - 1 - scrollPos + (animTicks > 0 && !animDir ? -1 : 0)));
	}
	
	public int getMainPileCardCount()
	{
		return getCurrentCardCount() - scrollPos - (animTicks > 0 && !animDir ? 1 : 0);
	}
	
	public Card getOtherPileFace()
	{
		return getCollection().getCardAt(Math.max(0, Math.min(getCurrentCardCount() - 1, getCurrentCardCount() - scrollPos + (animTicks > 0 && animDir ? 1 : 0))));
	}
	
	public int getOtherPileCardCount()
	{
		return scrollPos + (animTicks > 0 && animDir ? -1 : 0);
	}
	
	public Card getMovingCard()
	{
		return getCollection().getCardAt(Math.max(0, getCurrentCardCount() - scrollPos + (!animDir ? -1 : 0)));
	}
	
	public int getCurrentCardCount()
	{
		return getCollection().getCardCount() - (isCardIncoming() ? 1 : 0);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(scale(120 + 40), scale(scrollPos > 0 ? 366 : 180));
	}
	
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(0, 0);
	}
	
	public class MDDiscardListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(MouseEvent event)
		{
			constructInfoIcon();
			collapsing = false;
		}
		
		@Override
		public void mouseExited(MouseEvent event)
		{
			Point p = event.getPoint();
			if (p.x < 0 || p.x >= getWidth() || p.y < 0 || p.y >= getHeight())
			{
				destroyInfoIcon();
				//scrollPos = 0;
				//getParent().invalidate();
				if (scrollPos > 0)
				{
					collapsing = true;
					animDir = false;
				}
				repaint();
			}
		}
	}
}
