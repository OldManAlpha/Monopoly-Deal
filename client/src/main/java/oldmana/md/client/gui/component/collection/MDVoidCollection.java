package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import oldmana.md.client.MDEventQueue.CardMove;
import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.CardCollection;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDVoidCollection extends MDCardCollectionUnknown
{
	private int stage; // Range: 0(invisible) to 30(fully visible)
	private double pos; // Range: -20 to 20
	private boolean dir;
	
	private int intoVoidTicks;
	
	public MDVoidCollection(CardCollection collection)
	{
		super(collection, 1);
		
		getClient().getScheduler().scheduleTask(new MDTask(1, true)
		{
			@Override
			public void run()
			{
				if (getCollection() == null)
				{
					return;
				}
				
				double diff = (23 - Math.max(8, Math.abs(pos))) * 0.045;
				pos += dir ? diff : -diff;
				if ((dir && pos > 20) || (!dir && pos < -20))
				{
					dir = !dir;
				}
				
				if (isCardIncoming() || isCardMovingFrom() || getClient().isDebugEnabled() || intoVoidTicks > 0)
				{
					stage = Math.min(30, stage + 1);
					repaint();
				}
				else if (stage > 0)
				{
					stage--;
					repaint();
				}
				
				intoVoidTicks = Math.max(intoVoidTicks - 1, 0);
			}
		});
	}
	
	@Override
	public void cardArrived()
	{
		super.cardArrived();
		intoVoidTicks = 30;
	}
	
	private boolean isCardMovingFrom()
	{
		if (getClient().getEventQueue().getCurrentTask() instanceof CardMove)
		{
			CardMove move = (CardMove) getClient().getEventQueue().getCurrentTask();
			return move.getFrom() == getCollection();
		}
		return false;
	}

	@Override
	public void update()
	{
		repaint();
	}

	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(scale(20), scale(20));
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		Graphics2D gv = (Graphics2D) g.create();
		if (getCollection() == null)
		{
			return;
		}
		if (stage > 0)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.rotate(Math.toRadians(pos), getWidth() / 2, getHeight() / 2);
			g.setColor(new Color(0, 0, 40));
			double size = (double) stage / 30;
			g.fillRoundRect((getWidth() / 2) - (int) (GraphicsUtils.getCardWidth() * size * 0.5), (getHeight() / 2) - 
					(int) (GraphicsUtils.getCardHeight() * size * 0.5), (int) (GraphicsUtils.getCardWidth() * size), 
					(int) (GraphicsUtils.getCardHeight() * size), scale(10 * size), scale(10 * size));
			
			if (intoVoidTicks > 0)
			{
				double intoVoidProg = intoVoidTicks / 30.0;
				gv.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				gv.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				gv.drawImage(Card.getBackGraphics(1), (getWidth() / 2) - (int) (GraphicsUtils.getCardWidth() * size * 0.5 * intoVoidProg), (getHeight() / 2) - 
						(int) (GraphicsUtils.getCardHeight() * size * 0.5 * intoVoidProg), (int) (GraphicsUtils.getCardWidth() * size * intoVoidProg), 
						(int) (GraphicsUtils.getCardHeight() * size * intoVoidProg), null);
			}
			
			if (getClient().isDebugEnabled())
			{
				g.setColor(Color.ORANGE);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(20 * size), getWidth(), getHeight());
			}
		}
	}
}
