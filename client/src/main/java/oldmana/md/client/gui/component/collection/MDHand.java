package oldmana.md.client.gui.component.collection;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.gui.component.MDOverlayHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.common.net.packet.client.action.PacketActionMoveHandCard;

public class MDHand extends MDCardCollection
{
	private Card hovered;
	private MDOverlayHand overlay;
	private MDHandListener listener;
	
	private Card dragged;
	private int dragX;
	
	public MDHand(Hand hand)
	{
		super(hand, 2);
		listener = new MDHandListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		Toolkit.getDefaultToolkit().addAWTEventListener(event ->
		{
			if (overlay == null)
			{
				return;
			}
			if (event instanceof MouseEvent)
			{
				MouseEvent me = (MouseEvent) event;
				if (me.getID() == MouseEvent.MOUSE_MOVED)
				{
					if (me.getComponent() != MDHand.this && !SwingUtilities.isDescendingFrom(me.getComponent(), overlay))
					{
						removeOverlay();
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
		update();
	}
	
	@Override
	public void update()
	{
		updateGraphics();
	}
	
	public void removeOverlay()
	{
		if (hovered != null)
		{
			overlay.removeCardInfo();
			remove(overlay);
			hovered = null;
			overlay = null;
			updateGraphics();
		}
	}
	
	private int getCardX(int cardIndex, int cardCount)
	{
		if (getWidth() <= GraphicsUtils.getCardWidth(2))
		{
			return 0;
		}
		double cardsWidth = GraphicsUtils.getCardWidth(2) * cardCount;
		double padding = (getWidth() - cardsWidth) / (double) (cardCount + 1);
		boolean negativePadding = padding < 0;
		padding = Math.max(padding, 0);
		
		double start = padding - ((padding / cardCount) * cardIndex);
		
		double room = getWidth() - cardsWidth;
		double interval = room / (cardCount - (negativePadding ? 1 : 0));
		
		return (int) (start + ((GraphicsUtils.getCardWidth(2) + interval) * cardIndex));
	}
	
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(getCardX(cardIndex, cardCount), 0);
	}
	
	public Card getCardAt(int x, int y)
	{
		CardCollection hand = getCollection();
		for (int i = 0 ; i < hand.getCardCount() ; i++)
		{
			int low = getCardX(i, getCardCount());
			int high = low + GraphicsUtils.getCardWidth(2);
			if (x >= low && x < high)
			{
				return hand.getCardAt(i);
			}
		}
		return null;
	}
	
	public int getCardStartX(int x)
	{
		CardCollection hand = getCollection();
		for (int i = 0 ; i < hand.getCardCount() ; i++)
		{
			int low = getCardX(i, getCardCount());
			int high = low + GraphicsUtils.getCardWidth(2);
			if (x >= low && x < high)
			{
				return low;
			}
		}
		return -1;
	}
	
	@Override
	public void setModification(CollectionMod mod)
	{
		super.setModification(mod);
		removeOverlay();
		dragged = null;
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		
		CardCollection hand = getCollection();
		
		if (hand != null)
		{
			//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			paintCards(g);
			
			if (getClient().isDebugEnabled())
			{
				g.setColor(Color.MAGENTA);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(32), getWidth(), getHeight() / 2);
				
				g.setColor(Color.GREEN);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
		}
		else
		{
			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
	
	@Override
	public void paintCards(Graphics2D g)
	{
		for (Entry<Card, Point> entry : getCurrentCardPositions().entrySet())
		{
			Card card = entry.getKey();
			if (card == dragged)
			{
				continue;
			}
			Point p = entry.getValue();
			g.drawImage(card.getGraphics(getScale() * getCardScale()), p.x, p.y, null);
		}
		if (dragged != null)
		{
			g.drawImage(dragged.getGraphics(getScale() * getCardScale()),
					dragX - GraphicsUtils.getCardWidth(getCardScale() / 2), 0, null);
		}
	}
	
	private int getClosestIndex(int x)
	{
		int cardCount = getCardCount();
		for (int i = 0 ; i < cardCount ; i++)
		{
			int cardX = getLocationOf(i, cardCount).x + GraphicsUtils.getCardWidth(getCardScale() / 2);
			if (cardX > x)
			{
				return i;
			}
		}
		return getCardCount();
	}
	
	public class MDHandListener implements MouseListener, MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent event)
		{
			if (dragged == null && !isBeingModified())
			{
				Card curHover = getCardAt(event.getX(), 0);
				if (curHover != null)
				{
					dragged = curHover;
					dragX = Math.max(GraphicsUtils.getCardWidth(getCardScale() / 2),
							Math.min(event.getX(), getWidth() - GraphicsUtils.getCardWidth(getCardScale() / 2)));
					removeOverlay();
				}
			}
			if (dragged != null)
			{
				dragX = Math.max(GraphicsUtils.getCardWidth(getCardScale() / 2),
						Math.min(event.getX(), getWidth() - GraphicsUtils.getCardWidth(getCardScale() / 2)));
				updateGraphics();
			}
		}
		
		@Override
		public void mousePressed(MouseEvent event)
		{
		
		}
		
		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (dragged == null)
			{
				return;
			}
			int index = getClosestIndex(dragX);
			int cardIndex = getCollection().getIndexOf(dragged);
			if (index != cardIndex && index != cardIndex + 1) // These two indices would put the card in the same place
			{
				getClient().sendPacket(new PacketActionMoveHandCard(dragged.getID(), index));
			}
			dragged = null;
			updateGraphics();
		}

		@Override
		public void mouseMoved(MouseEvent event)
		{
			if (getCollection() != null)
			{
				if (dragged != null)
				{
					return;
				}
				Card curHover = getCardAt(event.getX(), 0);
				if (!isBeingModified())
				{
					if ((curHover == null && hovered != null) || (hovered != null && curHover != hovered))
					{
						removeOverlay();
					}
					if (curHover != null && hovered == null)
					{
						hovered = curHover;
						overlay = new MDOverlayHand(hovered);
						overlay.setLocation(getCardStartX(event.getX()), 0);
						add(overlay);
						overlay.addCardInfo();
						updateGraphics();
					}
				}
				else
				{
					removeOverlay();
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent event)
		{
			
		}

		@Override
		public void mouseEntered(MouseEvent event)
		{
		
		}

		@Override
		public void mouseExited(MouseEvent event)
		{
			if (getCollection() != null)
			{
				Point p = event.getPoint();
				if (p.x < 0 || p.x >= getWidth() || p.y < 0 || p.y >= getHeight())
				{
					if (hovered != null)
					{
						removeOverlay();
					}
				}
				updateGraphics();
			}
		}
	}
}
