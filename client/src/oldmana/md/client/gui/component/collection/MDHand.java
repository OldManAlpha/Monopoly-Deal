package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import oldmana.md.client.Player;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.CardActionActionCounter;
import oldmana.md.client.card.CardActionRentCounter;
import oldmana.md.client.card.CardProperty;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.card.collection.Hand;
import oldmana.md.client.gui.component.overlay.MDOverlayHand;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.state.ActionState;
import oldmana.md.client.state.ActionStateDiscard;
import oldmana.md.client.state.ActionStateRent;

public class MDHand extends MDCardCollection
{
	private Card hovered;
	private MDOverlayHand overlay;
	private MDHandListener listener;
	
	public MDHand(Hand hand)
	{
		super(hand, 2);
		listener = new MDHandListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		update();
	}
	
	@Override
	public void update()
	{
		/*
		boolean adding = addition != null;
		boolean removing = removal != null;
		boolean motion = adding || removing;
		if (getMDCards().size() > 0)
		{
			int interval = getWidth() / getMDCards().size();
			int start = (getWidth() / (getMDCards().size() * 2)) - MDCard.CARD_SIZE.width + 5;
			int motionInterval = 0;
			int motionStart = 0;
			if (adding)
			{
				motionInterval = ((getWidth() / (getMDCards().size() + 1)) + interval) / 2;
				motionStart = (((getWidth() / ((getMDCards().size() + 1) * 2)) - MDCard.CARD_SIZE.width + 5) + start) / 2;
			}
			if (removing)
			{
				motionInterval = ((getWidth() / (getMDCards().size() - 1)) + interval) / 2;
				motionStart = (((getWidth() / ((getMDCards().size() - 1) * 2)) - MDCard.CARD_SIZE.width + 5) + start) / 2;
			}
			for (int i = motion ? 1 : 0 ; i < getMDCards().size() ; i++)
			{
				getMDCards().get(motion ? i - 1 : i).setLocation(motion ? motionStart + (motionInterval * i) : start + (interval * i), 0);
			}
		}
		*/
		repaint();
	}
	
	/*
	private Card addition;
	private Card removal;
	private double motProg;
	*/
	
	/*
	@Override
	public void startAddition(Card card)
	{
		addition = card;
	}

	@Override
	public void startRemoval(Card card)
	{
		removal = card;
	}

	@Override
	public void updateMotion(double progress)
	{
		motProg = progress;
		update();
	}

	@Override
	public void motionFinished()
	{
		if (addition != null)
		{
			addCard(addition.getUI());
			addition = null;
		}
		else
		{
			removeCard(removal.getUI());
			removal = null;
		}
	}
	*/
	
	public void removeOverlay()
	{
		if (hovered != null)
		{
			remove(overlay);
			hovered = null;
			overlay = null;
			repaint();
		}
	}
	
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		double interval = getWidth() / (double) cardCount;
		double start = (getWidth() / (cardCount * 2.0)) - GraphicsUtils.getCardWidth();
		int x = (int) (start + (interval * cardIndex));
		return new Point(x, 0);
	}
	
	public Card getCardAt(int x, int y)
	{
		CardCollection hand = getCollection();
		double interval = getWidth() / (double) hand.getCardCount();
		double start = (getWidth() / (hand.getCardCount() * 2.0)) - GraphicsUtils.getCardWidth();
		for (int i = 0 ; i < hand.getCardCount() ; i++)
		{
			int low = (int) (start + (interval * i));
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
		double interval = getWidth() / (double) hand.getCardCount();
		double start = (getWidth() / (hand.getCardCount() * 2.0)) - GraphicsUtils.getCardWidth();
		for (int i = 0 ; i < hand.getCardCount() ; i++)
		{
			int low = (int) (start + (interval * i));
			int high = low + GraphicsUtils.getCardWidth(2);
			if (x >= low && x < high)
			{
				return low;
			}
		}
		return -1;
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		
		CardCollection hand = getCollection();
		
		if (hand != null)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
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
	
	public class MDHandListener implements MouseListener, MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent event)
		{
			
		}

		@Override
		public void mouseMoved(MouseEvent event)
		{
			if (getCollection() != null)
			{
				Player player = getClient().getThePlayer();
				ActionState state = getClient().getGameState().getActionState();
				Card curHover = getCardAt(event.getX(), 0);
				boolean canPlayJSN = curHover instanceof CardActionActionCounter && state != null && ((state.isTarget(player) && 
						!state.isRefused(player) && !state.isAccepted(player)) || (state.getActionOwner() == player && state.getNumberOfRefused() > 0)) 
						&& !state.isUsingJustSayNo();
				boolean canPlayRentCounter = curHover instanceof CardActionRentCounter && state != null && state instanceof ActionStateRent && 
						state.isTarget(player) && !state.isRefused(player) && !state.isAccepted(player);
				boolean discard = state instanceof ActionStateDiscard;
				boolean property = curHover instanceof CardProperty;
				if (!getClient().isInputBlocked() && (getClient().canPlayCard() || canPlayJSN || canPlayRentCounter || (discard && state.getActionOwner() == player && 
						(!property || player.hasAllPropertiesInHand()))))
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
						repaint();
					}
				}
				else if (getClient().isInputBlocked() || (!getClient().canPlayCard() && !canPlayJSN))
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
			/*
			System.out.println("Mouse Entered");
			MDCard card = (MDCard) event.getComponent();
			if (overlay != null && overlay.getCard() != card)
			{
				MDCard last = overlay.getCard();
				last.remove(overlay);
				last.repaint();
				overlay = null;
			}
			if (overlay == null)
			{
				overlay = new MDOverlayHand(card);
				card.add(overlay);
				card.repaint();
			}
			*/
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
						hovered = null;
						if (overlay != null)
						{
							remove(overlay);
							overlay = null;
						}
					}
				}
				repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent event)
		{
			
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			
		}
	}
}
