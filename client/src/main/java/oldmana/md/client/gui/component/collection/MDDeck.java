package oldmana.md.client.gui.component.collection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import oldmana.md.client.Scheduler;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.gui.component.MDSelection;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;
import oldmana.md.client.gui.util.TextPainter.Outline;

public class MDDeck extends MDCardCollectionUnknown
{
	private boolean hovered;
	private double animProgress;
	
	private MDSelection notifySelect;
	
	public MDDeck(Deck deck)
	{
		super(deck, 2);
		MDDeckListener listener = new MDDeckListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		constructNotifySelect();
		getClient().getScheduler().scheduleFrameboundTask(task ->
		{
			double lastStage = animProgress;
			if (getClient().isInputBlocked() || !getClient().canDraw())
			{
				animProgress = 0;
				notifySelect.setVisible(false);
			}
			else
			{
				if (hovered)
				{
					animProgress = Math.min(animProgress + (Scheduler.getFrameDelay() / ((animProgress + 40) / 120)), 250);
					notifySelect.setVisible(false);
				}
				else
				{
					animProgress = Math.max(animProgress - Scheduler.getFrameDelay(), 0);
					if (animProgress == 0)
					{
						notifySelect.setVisible(true);
						notifySelect.updateGraphics();
					}
				}
			}
			if (lastStage != animProgress)
			{
				updateGraphics();
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent event)
			{
				constructNotifySelect();
			}
		});
		update();
	}
	
	public void constructNotifySelect()
	{
		MDSelection select = new MDSelection();
		select.setSize(scale(120), scale(180));
		select.setLocation(0, scale(10));
		select.setColor(Color.BLUE);
		if (notifySelect != null)
		{
			select.setVisible(notifySelect.isVisible());
			remove(notifySelect);
		}
		else
		{
			select.setVisible(false);
		}
		add(select);
		notifySelect = select;
		updateGraphics();
	}
	
	@Override
	public void update()
	{
		updateGraphics();
	}
	
	@Override
	public boolean shouldAnimateModification()
	{
		return false;
	}
	
	@Override
	public Point getLocationOf(int cardIndex, int cardCount)
	{
		return new Point(0, scale(10));
	}
	
	@Override
	public void doPaint(Graphics gr)
	{
		super.doPaint(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getCollection() != null)
		{
			int cardCount = getCollection().getCardCount();
			if (cardCount > 0 && !(cardCount <= 2 && animProgress > 0))
			{
				g.translate(0, scale(10));
				drawCardStack(g, scale(60), 0, scale(60) + (int) Math.floor(cardCount * (0.3 * GraphicsUtils.SCALE)));
				g.drawImage(Card.getBackGraphics(GraphicsUtils.SCALE * 2), 0, 0, null);
			}
			else
			{
				g.setColor(Color.LIGHT_GRAY);
				g.translate(0, scale(10));
				g.fillRoundRect(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2), scale(20), scale(20));
				g.setColor(Color.BLACK);
				Font font = GraphicsUtils.getThinMDFont(Font.PLAIN, scale(18));
				g.setFont(font);
				TextPainter tp = new TextPainter("Deck Empty", font, new Rectangle(0, 0, GraphicsUtils.getCardWidth(2), GraphicsUtils.getCardHeight(2)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			
			if (animProgress > 0)
			{
				for (int i = 2 ; i > 0 ; i--)
				{
					if (getCardCount() < i)
					{
						continue;
					}
					BufferedImage img = GraphicsUtils.createImage(GraphicsUtils.getCardWidth(2) + scale(20),
							GraphicsUtils.getCardHeight(2) + scale(24));
					Graphics2D g2 = img.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2.rotate(Math.toRadians(animProgress * 0.026 * (i == 2 ? 0.3 : 1)), 0, GraphicsUtils.getCardHeight(2) + scale(10));
					g2.translate(0, scale(10));
					g2.drawImage(Card.getBackGraphics(GraphicsUtils.SCALE * 2), 0, 0, null);
					int width = GraphicsUtils.getCardWidth(2);
					int height = GraphicsUtils.getCardHeight(2);
					g2.setColor(Color.LIGHT_GRAY);
					g2.drawRoundRect(0, 0, width, height, width / 6, height / 6);
					g2.setColor(Color.WHITE);
					g2.drawRoundRect(1, 1, width - 2, height - 2, (width / 6) - 2, (height / 6) - 2);
					g2.dispose();
					g.translate(0, scale(-10));
					g.drawImage(img, 0, 0, null);
					g.translate(0, scale(10));
				}
			}
			else if (cardCount <= 8 && cardCount > 0)
			{
				g.setColor(Color.BLACK);
				
				Font font = GraphicsUtils.getBoldMDFont(Font.PLAIN, scale(20));
				g.setFont(font);
				TextPainter tp = new TextPainter(getCollection().getCardCount() + " Card" + (cardCount != 1 ? "s" : ""), font, new Rectangle(0, scale(2), GraphicsUtils.getCardWidth(2), 
						GraphicsUtils.getCardHeight(2)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.setOutline(Outline.of(Color.WHITE, scale(4)));
				tp.paint(g);
			}
			
			if (getClient().isDebugEnabled())
			{
				g = (Graphics2D) gr.create();
				g.setColor(Color.ORANGE);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(30), GraphicsUtils.getCardWidth(2), getHeight() / 2);
			}
		}
		//g.fillRect(60, 0, collection.getCardCount(), 90);
	}
	
	public class MDDeckListener implements MouseListener, MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent event) {}

		@Override
		public void mouseMoved(MouseEvent event) {}

		@Override
		public void mouseClicked(MouseEvent event) {}

		@Override
		public void mouseEntered(MouseEvent event)
		{
			hovered = true;
			updateGraphics();
		}

		@Override
		public void mouseExited(MouseEvent event)
		{
			hovered = false;
			updateGraphics();
		}

		@Override
		public void mousePressed(MouseEvent event) {}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (getClient().canDraw() && !getClient().isInputBlocked())
			{
				getClient().draw();
				updateGraphics();
			}
		}
	}
	
	public static void drawCardStack(Graphics2D g, int x, int y, int width)
	{
		Color stack = new Color(80, 80, 80);
		Color shine = new Color(140, 140, 140);
		LinearGradientPaint paint = new LinearGradientPaint(0, y, 0, GraphicsUtils.getCardHeight(2) + y,
				new float[] {0, 0.025F, 0.05F, 0.95F, 0.975F, 1F},
				new Color[] {stack, shine, stack, stack, shine, stack});
		g.setPaint(paint);
		g.fillRoundRect(x, y, width, GraphicsUtils.scale(180), GraphicsUtils.scale(20), GraphicsUtils.scale(20));
	}
}
