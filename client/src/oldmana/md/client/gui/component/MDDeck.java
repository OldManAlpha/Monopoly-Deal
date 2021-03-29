package oldmana.md.client.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import oldmana.md.client.MDScheduler.MDTask;
import oldmana.md.client.card.Card;
import oldmana.md.client.card.collection.Deck;
import oldmana.md.client.gui.util.CardPainter;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.client.gui.util.TextPainter;
import oldmana.md.client.gui.util.TextPainter.Alignment;

public class MDDeck extends MDCardCollectionUnknown
{
	private boolean hovered;
	private MDTask animTask;
	private int animStage;
	
	private MDSelection notifySelect;
	
	public MDDeck(Deck deck)
	{
		super(deck, 2);
		MDDeckListener listener = new MDDeckListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		constructNotifySelect();
		animTask = new MDTask(1, true)
		{
			@Override
			public void run()
			{
				int lastStage = animStage;
				if (getClient().isInputBlocked() || !getClient().canDraw())
				{
					animStage = 0;
					notifySelect.setVisible(false);
				}
				else
				{
					if (hovered)
					{
						animStage = Math.min(animStage + 1, 16);
						notifySelect.setVisible(false);
					}
					else
					{
						animStage = Math.max(animStage - 1, 0);
						if (animStage == 0)
						{
							notifySelect.setVisible(true);
							notifySelect.repaint();
						}
					}
				}
				if (lastStage != animStage)
				{
					repaint();
				}
			}
		};
		getClient().getScheduler().scheduleTask(animTask);
		this.addComponentListener(new ComponentAdapter()
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
		repaint();
	}
	
	/*
	@Override
	public void addCard(MDCard card)
	{
		
	}
	
	@Override
	public void removeCard(MDCard card)
	{
		
	}
	*/
	
	@Override
	public void update()
	{
		/*
		if (getCollection().getCardCount() == 0)
		{
			if (face != null)
			{
				remove(face);
				face = null;
			}
		}
		else
		{
			if (face == null)
			{
				face = new MDCard(null);
				face.setScale(2);
				face.setLocation(0, 0);
				add(face);
			}
		}
		*/
		repaint();
	}
	
	@Override
	public Point getLocationOf(int cardIndex)
	{
		return SwingUtilities.convertPoint(this, new Point(0, scale(10)), getClient().getTableScreen());
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getCollection() != null)
		{
			int cardCount = getCollection().getCardCount();
			if (cardCount > 0 && !(cardCount == 1 && animStage > 0))
			{
				g.translate(0, scale(10));
				g.setColor(Color.DARK_GRAY);
				g.fillRoundRect(scale(60), 0, scale(60) + (int) Math.floor(cardCount * (0.3 * GraphicsUtils.SCALE)), 
						scale(180), scale(20), scale(20));
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
			
			if (animStage > 0)
			{
				BufferedImage img = GraphicsUtils.createImage(GraphicsUtils.getCardWidth(2) + scale(16), GraphicsUtils.getCardHeight(2) + scale(24));
				Graphics2D g2 = img.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.rotate(Math.toRadians(animStage * 0.35), 0, GraphicsUtils.getCardHeight(2) + scale(10));
				g2.translate(0, scale(10));
				CardPainter cp = new CardPainter(null, GraphicsUtils.SCALE * 2);
				cp.paint(g2);
				//g2.drawImage(Card.getBackGraphics(GraphicsUtils.SCALE * 2), 0, 0, null);
				g.translate(0, scale(-10));
				g.drawImage(img, 0, 0, null);
			}
			else if (cardCount <= 8 && cardCount > 0)
			{
				g.setColor(new Color(240, 240, 240));
				g.fillRoundRect(GraphicsUtils.getCardWidth(0.4), GraphicsUtils.getCardHeight(1) - scale(10), GraphicsUtils.getCardWidth(1.2), scale(20), scale(8), scale(8));
				g.setColor(Color.BLACK);
				g.drawRoundRect(GraphicsUtils.getCardWidth(0.4), GraphicsUtils.getCardHeight(1) - scale(10), GraphicsUtils.getCardWidth(1.2), scale(20), scale(8), scale(8));
				
				Font font = GraphicsUtils.getBoldMDFont(Font.PLAIN, scale(20));
				g.setFont(font);
				TextPainter tp = new TextPainter(getCollection().getCardCount() + " Card" + (cardCount != 1 ? "s" : ""), font, new Rectangle(0, scale(2), GraphicsUtils.getCardWidth(2), 
						GraphicsUtils.getCardHeight(2)));
				tp.setHorizontalAlignment(Alignment.CENTER);
				tp.setVerticalAlignment(Alignment.CENTER);
				tp.paint(g);
			}
			
			if (getClient().isDebugEnabled())
			{
				g = (Graphics2D) gr.create();
				g.setColor(Color.ORANGE);
				GraphicsUtils.drawDebug(g, "ID: " + getCollection().getID(), scale(30), getWidth(), getHeight() / 2);
			}
		}
		//g.fillRect(60, 0, collection.getCardCount(), 90);
	}
	
	public class MDDeckListener implements MouseListener, MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent event)
		{
			hovered = true;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent event)
		{
			hovered = false;
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent event)
		{
			if (getClient().canDraw() && !getClient().isInputBlocked())
			{
				getClient().draw();
			}
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
