package oldmana.md.client.gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.util.GraphicsUtils;

public class MDMovingCard extends MDComponent
{
	private double progress;
	
	private Card start;
	private Card end;
	
	private Point startPos;
	private Point endPos;
	private double startScale;
	private double endScale;
	private double speed;
	
	private BufferedImage[] cardCache = new BufferedImage[2];
	
	private double[] progMap;
	private int pos;
	private BufferedImage[] frameCache;
	
	
	
	public MDMovingCard(Card card, Point startPos, double startScale, Point endPos, double endScale, double speed)
	{
		this(card, startPos, startScale, card, endPos, endScale, speed);
	}
	
	public MDMovingCard(Card start, Point startPos, double startScale, Card end, Point endPos, double endScale, double speed)
	{
		super();
		this.startPos = startPos;
		this.endPos = endPos;
		this.startScale = startScale;
		this.endScale = endScale;
		this.start = start;
		this.end = end;
		int cardWidth = GraphicsUtils.getCardWidth();
		int cardHeight = GraphicsUtils.getCardHeight();
		setSize((int) (cardWidth * startScale) + scale(10), (int) (cardHeight * startScale) + scale(10));
		setLocation(startPos.x - scale(5), startPos.y - scale(5));
		
		double largestScale = Math.max(startScale, endScale);
		int largestWidth = (int) Math.round(largestScale * cardWidth);
		int largestHeight = (int) Math.round(largestScale * cardHeight);
		BufferedImage img = GraphicsUtils.createImage(largestWidth, largestHeight);
		Graphics2D cardGr = img.createGraphics();
		cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cardGr.drawImage(start != null ? start.getGraphics(largestScale * getScale()) : Card.getBackGraphics(largestScale * getScale()), 0, 0, largestWidth, largestHeight, null);
		cardCache[0] = img;
		
		img = GraphicsUtils.createImage(largestWidth, largestHeight);
		cardGr = img.createGraphics();
		cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cardGr.drawImage(end != null ? end.getGraphics(largestScale * getScale()) : Card.getBackGraphics(largestScale * getScale()), 0, 0, largestWidth, largestHeight, null);
		cardCache[1] = img;
		
		int size = 0;
		while (progress < 1)
		{
			progress += 0.02 * Math.abs(progress - 1.4) * speed;
			size++;
		}
		progMap = new double[size];
		progress = 0;
		int i = 0;
		while (progress < 1)
		{
			progress += 0.02 * Math.abs(progress - 1.4) * speed;
			progMap[i] = progress;
			i++;
		}
		if (start != end)
		{
			frameCache = new BufferedImage[size];
			startCachingFrames();
		}
		//end.getOwningCollection().getUI().startAddition(end);
		/*
		MDClient.getInstance().getScheduler().scheduleTask(new MDTask(1, true)
		{
			//boolean dir = true;
			@Override
			public void run()
			{
				//progress += 0.02 * Math.abs(progress - 1.4);
				pos++;
				if (pos == progMap.length - 2)
				{
					cancel();
					//end.getOwningCollection().getUI().motionFinished();
					getParent().remove(MDMovingCard.this);
				}
				else
				{
					//end.getOwningCollection().getUI().updateMotion(progMap[pos]);
				}
				
				double scale = (startScale * 1) + ((endScale - startScale) * progMap[pos]);
				
				setSize((int) Math.round(scale * cardWidth), 
						(int) Math.round(scale * cardHeight));
				setLocation((int) Math.round(startPos.x + ((endPos.x - startPos.x) * progMap[pos])), 
						(int) Math.round(startPos.y + ((endPos.y - startPos.y) * progMap[pos])));
				
				repaint();
			}
		});
		*/
	}
	
	public boolean tickMove()
	{
		int cardWidth = GraphicsUtils.getCardWidth();
		int cardHeight = GraphicsUtils.getCardHeight();
		
		pos++;
		
		double scale = (startScale * 1) + ((endScale - startScale) * progMap[pos]);
		
		setSize((int) Math.round(scale * cardWidth) + 10, 
				(int) Math.round(scale * cardHeight) + 10);
		setLocation((int) Math.round(startPos.x + ((endPos.x - startPos.x) * progMap[pos])) - 5, 
				(int) Math.round(startPos.y + ((endPos.y - startPos.y) * progMap[pos])) - 5);
		
		repaint();
		if (pos == progMap.length - 2)
		{
			return true;
		}
		return false;
	}
	
	public double getLargestScale()
	{
		return Math.max(startScale, endScale);
	}
	
	public int getLargestWidth()
	{
		return GraphicsUtils.getCardWidth(getLargestScale());
	}
	
	public int getLargestHeight()
	{
		return GraphicsUtils.getCardHeight(getLargestScale());
	}
	
	public void startCachingFrames()
	{
		Platform.runLater(() -> {
			for (int i = 0 ; i < progMap.length ; i++)
			{
				double progress = Math.min(progMap[i] * 1.2, 1);
				BufferedImage image = GraphicsUtils.createImage(cardCache[0].getWidth(), cardCache[0].getHeight());
				image.createGraphics().drawImage(cardCache[progress < 0.5 ? 0 : 1], 0, 0, null);
		        int width = image.getWidth();
		        int height = image.getHeight();
		        Canvas canvas = new Canvas(width, height);
		        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
		
		        PerspectiveTransform trans = new PerspectiveTransform();
		        
		        boolean dir = progress < 0.5;
		        double prog = progress < 0.5 ? progress * 2 : Math.abs(progress - 1) * 2;
		        
		        // TOP
		        trans.setUlx((width / 2) * prog); // LEFT
		        trans.setUrx(width - ((width / 2) * prog)); // RIGHT
		        
		        // BOTTOM
		        trans.setLlx((width / 2) * prog); // LEFT
		        trans.setLrx(width - ((width / 2) * prog)); // RIGHT
		        
		        // LEFT
		        trans.setUly(0 + (dir ? prog * (height / 6) : 0)); // TOP
		        trans.setLly(height - (dir ? prog * (height / 6) : 0)); // BOTTOM
		        
		        // RIGHT
		        trans.setUry(0 + (!dir ? prog * (height / 6) : 0)); // TOP
		        trans.setLry(height - (!dir ? prog * (height / 6) : 0)); // BOTTOM
		
		        imageView.setEffect(trans);
		
		        //imageView.setRotate(2);
		
		        SnapshotParameters params = new SnapshotParameters();
		        params.setFill(Color.TRANSPARENT);
		
		        Image newImage = imageView.snapshot(params, null);
		        graphicsContext.drawImage(newImage, 0, 0);
		
		        synchronized (frameCache)
		        {
		        	BufferedImage from = SwingFXUtils.fromFXImage(newImage, image);;
		        	frameCache[i] = GraphicsUtils.createImage(width, height);
		        	frameCache[i].createGraphics().drawImage(from, (int) ((width / 2) * prog), 0, null);
		        }
		        //System.out.println("Cached: " + i);
			}
        });
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		int width = getLargestWidth();
		int height = getLargestHeight();
		double progress = Math.min(progMap[pos] * 1.2, 1);
		/*
		BufferedImage img = null;
		if (cache == null)
		{
			img = GraphicsUtils.createImage(width, height);
			CardPainter cp = new CardPainter(progress < 0.5 ? start : end, width, height, startScale);
			cp.paint(img.createGraphics());
			cache = img;
		}
		else
		{
			img = GraphicsUtils.createImage(width, height);
			img.createGraphics().drawImage(cache, 0, 0, null);
		}
		*/
		BufferedImage result = null;
		int pos = this.pos - 1;
		int i = 0;
		if (pos >= 0 && start != end)
		{
			while (frameCache[pos] == null)
			{
				i++;
				//System.out.println("Pickle Rick x" + i);
				try
				{
					Thread.sleep(1); // I'M PICKLE REEEE
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			synchronized (frameCache)
			{
				BufferedImage img = GraphicsUtils.createImage(width, height);
				if (progress >= 1)
				{
					Graphics2D cardGr = img.createGraphics();
					cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					cardGr.drawImage(progress < 0.5 ? cardCache[0] : cardCache[1], 0, 0, width, height, null);
					result = img;
				}
				else
				{
					result = frameCache[pos];
				}
			}
		}
		else
		{
			BufferedImage img = GraphicsUtils.createImage(width, height);
			img.createGraphics().drawImage(progress < 0.5 ? cardCache[0] : cardCache[1], 0, 0, null);
			result = img;
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(result, 5, 5, getWidth() - 10, getHeight() - 10, null);
		// TODO: Fix weird rendering cutoff issue
		//System.out.println("WIDTH: " + getWidth() + " | IMG WIDTH: " + result.getWidth());
		//System.out.println("Painted: " + pos);
	}
}
