package oldmana.md.client.gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

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

/**The biggest shitshow of a class ever made.
 * 
 */
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
	
	public double[] progMap;
	public int pos;
	private BufferedImage[] frameCache;
	private volatile int framePos;
	
	private AnimationType animType = AnimationType.NORMAL;
	
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
		int frameSize = 0;
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
			if (frameSize == 0 && progress * 1.2 > 1)
			{
				frameSize = i;
			}
			i++;
		}
		if (start != end || animType == AnimationType.IMPORTANT)
		{
			frameCache = new BufferedImage[frameSize];
			startCachingFrames();
		}
	}
	
	public boolean tickMove()
	{
		/*
		 * float prog = (System.currentTimeMillis() % 1000) / 1000F;
                    float prog2 = (float) (prog > 0.5 ? Math.abs(prog - 1) : prog);
		 */
		
		
		int cardWidth = GraphicsUtils.getCardWidth();
		int cardHeight = GraphicsUtils.getCardHeight();
		
		if (frameCache != null && pos - 1 > 0 && frameCache.length > pos)
		{
			frameCache[pos - 1] = null;
		}
		pos++;
		
		double prog = progMap[pos];
		
		if (animType == AnimationType.NORMAL)
		{
			double scale = (startScale * 1) + ((endScale - startScale) * prog);
			
			setSize((int) Math.round(scale * cardWidth) + 30, 
					(int) Math.round(scale * cardHeight) + 30);
			setLocation((int) Math.round(startPos.x + ((endPos.x - startPos.x) * prog)) - 15, 
					(int) Math.round(startPos.y + ((endPos.y - startPos.y) * prog)) - 15);
		}
		else if (animType == AnimationType.IMPORTANT)
		{
			// 0 - 0.25: Moving To Front
			// 0.25 - 0.75: Front
			// 0.75 - 1: Moving To Destination
			if (prog < 0.25)
			{
				double scale = (startScale * 1) + ((4 - startScale) * getProgressBetween(0, 0.25, prog));
				setSize((int) Math.round(scale * cardWidth) + 30, 
						(int) Math.round(scale * cardHeight) + 30);
				int xPos = (getParent().getWidth() / 2) - (getWidth() / 2);
				int yPos = (getParent().getHeight() / 2) - (getHeight() / 2);
				setLocation((int) Math.round(startPos.x + ((xPos - startPos.x) * getProgressBetween(0, 0.25, prog))) - 15, 
						(int) Math.round(startPos.y + ((yPos - startPos.y) * getProgressBetween(0, 0.25, prog))) - 15);
			}
			else if (prog > 0.75)
			{
				double scale = (4) + ((endScale - 4) * getProgressBetween(0.75, 1, prog));
				setSize((int) Math.round(scale * cardWidth) + 30, 
						(int) Math.round(scale * cardHeight) + 30);
				int xPos = (getParent().getWidth() / 2) - (getWidth() / 2);
				int yPos = (getParent().getHeight() / 2) - (getHeight() / 2);
				// 10
				// 5
				// 10 + (5 - 10 * prog)
				// 10 to -5
				setLocation((int) Math.round(xPos + ((endPos.x - xPos) * getProgressBetween(0.75, 1, prog))) - 15, 
						(int) Math.round(yPos + ((endPos.y - yPos) * getProgressBetween(0.75, 1, prog))) - 15);
			}
			else
			{
				setSize((int) Math.round(4 * cardWidth) + 30, 
						(int) Math.round(4 * cardHeight) + 30);
			}
		}
		repaint();
		if (pos == progMap.length - 2)
		{
			return true;
		}
		return false;
	}
	
	private double getProgressBetween(double start, double end, double num)
	{
		return (num - start) / (end - start);
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
	
	/**Must be executed on the JavaFX thread
	 * 
	 * @param degrees
	 * @return
	 */
	/*
	public RotatedCard renderRotatedCard(double degrees, boolean doShine)
	{
		degrees -= 90;
		degrees = (degrees + 360) % 360;
		degrees =- (startFacing ? 180 : 0);
		boolean face = degrees > 180;
		double angle = Math.toRadians(degrees);
		
		// Shine Effect
		BufferedImage image = GraphicsUtils.createImage(realCache.getWidth(), realCache.getHeight());
		int width = image.getWidth();
        int height = image.getHeight();
		Graphics2D g = image.createGraphics();
		g.drawImage(renderedCard != null ? realCache : Card.getBackGraphics(getLargestScale()), 0, 0, null);
		if (doShine)
		{
			if (degrees > 0 && degrees < 90)
			{
				double shineProg = degrees / 90.0;
				int widthPos = (int) (((shineProg - 0.5) * 2) * width);
				int heightPos = (int) (((shineProg - 0.5) * 2) * height);
				java.awt.Color transparent = new java.awt.Color(255, 255, 255, 0);
				java.awt.Color shine = new java.awt.Color(255, 255, 255, 170);
				LinearGradientPaint paint = new LinearGradientPaint(widthPos, heightPos, widthPos + (width / 4), heightPos + (height / 4), 
						new float[] {0F, 0.45F, 0.55F, 1F}, new java.awt.Color[] {transparent, shine, shine, transparent});
	    		g.setPaint(paint);
	    		g.fillRect(0, 0, width, height);
			}
		}
		// End Shine Effect
		
		Canvas canvas = new Canvas(width, height);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));

        PerspectiveTransform trans = new PerspectiveTransform();
        
        final double radius = width / 2D;
        final double back = height / 20;
        
        trans.setUlx(radius - Math.sin(angle) * radius);
        trans.setUly(0 - Math.cos(angle) * back);
        trans.setUrx(radius + Math.sin(angle) * radius);
        trans.setUry(0 + Math.cos(angle) * back);
        trans.setLrx(radius + Math.sin(angle) * radius);
        trans.setLry(height - Math.cos(angle) * back);
        trans.setLlx(radius - Math.sin(angle) * radius);
        trans.setLly(height + Math.cos(angle) * back);
        
        
        imageView.setEffect(trans);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        Image newImage = imageView.snapshot(params, null);
        graphicsContext.drawImage(newImage, 0, 0);
        
        int drawWidth = (int) Math.abs((radius + Math.sin(angle) * radius) - (radius - Math.sin(angle) * radius));
        
        return new RotatedCard(SwingFXUtils.fromFXImage(newImage, image), drawWidth);
	}
	*/
	
	public void startCachingFrames()
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = framePos ; i < frameCache.length ; i++)
				{
					if (framePos > pos + 10)
					{
						try
						{
							Thread.sleep(1);
						}
						catch (InterruptedException e) {}
						Platform.runLater(this);
						return;
					}
					
					double realProg = progMap[i];
					double progress = Math.min(progMap[i] * 1.2, 1);
					double degrees = (progress * (180) - 90);
					if (animType == AnimationType.IMPORTANT)
					{
						if (realProg < 0.25)
						{
							degrees = getProgressBetween(0, 0.25, realProg) * (360 + 180 - 40 + (start == null ? 0 : 180)) - 90;
						}
						else if (realProg > 0.75)
						{
							degrees = (start == null ? 90 : -90)/* + 30 - (Math.min(1, getProgressBetween(0.75, 0.85, realProg)) * 30)*/ + (end == null ? (getProgressBetween(0.75, 1, realProg) * 180) : 0);
							//degrees = getProgressBetween(0.75, 1, realProg) * (180 + 360 + (start == null ? 180 : 0)) + (start == null ? 90 : -90);
						}
						else if (realProg < 0.6)
						{
							degrees = (start == null ? 90 : -90) - 40 + (getProgressBetween(0.25, 0.6, realProg) * 80);
						}
						else
						{
							degrees = (start == null ? 90 : -90) + 40 - (getProgressBetween(0.6, 0.75, realProg) * 40);
						}
					}
					degrees = (degrees + 360) % 360;
					boolean face = degrees > 180;
					double angle = Math.toRadians(degrees - (start != null ? 180 : 0));
					BufferedImage image = GraphicsUtils.createImage(cardCache[0].getWidth(), cardCache[0].getHeight());
					int width = image.getWidth();
			        int height = image.getHeight();
					Graphics2D g = image.createGraphics();
					g.drawImage(start == end && !face ? Card.getBackGraphics(getLargestScale()) : cardCache[/*progress < 0.5 ? 0 : 1*/face ? 0 : 1], 0, 0, null);
					if (progress > 0.5 && end != null)
					{
						int widthPos = (int) (((progress - 0.5) * 2) * width);
						int heightPos = (int) (((progress - 0.5) * 2) * height);
						java.awt.Color transparent = new java.awt.Color(255, 255, 255, 0);
						java.awt.Color shine = new java.awt.Color(255, 255, 255, 170);
						LinearGradientPaint paint = new LinearGradientPaint(widthPos, heightPos, widthPos + (width / 4), heightPos + (height / 4), 
								new float[] {0F, 0.45F, 0.55F, 1F}, new java.awt.Color[] {transparent, shine, shine, transparent});
		        		g.setPaint(paint);
		        		g.fillRect(0, 0, width, height);
					}
			        
			        Canvas canvas = new Canvas(width, height);
			        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
			        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
			
			        PerspectiveTransform trans = new PerspectiveTransform();
			        
			        //double angle = Math.toRadians((progress * (180 + 360)) - 90);
			        
		
		            final double radius = width / 2D;
		            final double back = height / 20D;
		            
		            trans.setUlx(radius - Math.sin(angle) * radius);
		            trans.setUly(0 - Math.cos(angle) * back);
		            trans.setUrx(radius + Math.sin(angle) * radius);
		            trans.setUry(0 + Math.cos(angle) * back);
		            trans.setLrx(radius + Math.sin(angle) * radius);
		            trans.setLry(height - Math.cos(angle) * back);
		            trans.setLlx(radius - Math.sin(angle) * radius);
		            trans.setLly(height + Math.cos(angle) * back);
		            
		            int drawWidth = (int) Math.abs((radius + Math.sin(angle) * radius) - (radius - Math.sin(angle) * radius));
		            
		            /*
		             * trans.setUlx(radius - Math.sin(angle) * radius * rotatedScale);
		            trans.setUly(0 - Math.cos(angle) * back * rotatedScale);
		            trans.setUrx(radius + Math.sin(angle) * radius * rotatedScale);
		            trans.setUry(0 + Math.cos(angle) * back * rotatedScale);
		            trans.setLrx(radius + Math.sin(angle) * radius * rotatedScale);
		            trans.setLry(height - Math.cos(angle) * back * rotatedScale);
		            trans.setLlx(radius - Math.sin(angle) * radius * rotatedScale);
		            trans.setLly(height + Math.cos(angle) * back * rotatedScale);
		             */
			        
		            /*
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
			        */
			
			        imageView.setEffect(trans);
			
			        //imageView.setRotationAxis(new Point3D(0, 1, 0));
			        //imageView.setRotate(ddegress - 90);
			
			        SnapshotParameters params = new SnapshotParameters();
			        params.setFill(Color.TRANSPARENT);
			
			        Image newImage = imageView.snapshot(params, null);
			        graphicsContext.drawImage(newImage, 0, 0);
			
			        synchronized (frameCache)
			        {
			        	BufferedImage from = SwingFXUtils.fromFXImage(newImage, image);
			        	frameCache[i] = GraphicsUtils.createImage(width, height);
			        	Graphics2D gg = frameCache[i].createGraphics();
			        	gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						gg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			        	gg.drawImage(from, /*(int) ((width / 2) * prog)*/(width / 2) - (drawWidth / 2), -1, drawWidth, height + 1, null);
			        	// Nothin' a little pickle rickin' won't fix
			        }
			        framePos++;
			        //System.out.println("Cached: " + i);
				}
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
		if (pos >= 0 && frameCache != null && progress < 1)
		{
			while (framePos < pos)
			{
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
			/*
			if (animType == AnimationType.IMPORTANT)
			{
				result = frameCache[pos];
			}
			else
			{
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
			*/
			synchronized (frameCache)
			{
				result = frameCache[pos];
			}
		}
		else
		{
			BufferedImage img = GraphicsUtils.createImage(width, height);
			img.createGraphics().drawImage(progress < 0.5 ? cardCache[0] : cardCache[1], 0, 0, width, height, null);
			result = img;
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		//if (animType == AnimationType.IMPORTANT && progMap[this.pos] > 0.25 && progMap[this.pos] < 0.75)
		{
			//double prog = 6.0 * Math.cos((((progMap[this.pos] - 0.25) * 1.5) + 0.25) * 8.0);
			//double rot = Math.toRadians(prog);//Math.toRadians((progMap[this.pos] - 0.5) * 15);
			//g.rotate(rot, getWidth() / 2, getHeight() / 2);
		}
		g.drawImage(result, 15, 15, getWidth() - 30, getHeight() - 30, null);
		//System.out.println("WIDTH: " + getWidth() + " | IMG WIDTH: " + result.getWidth());
		//System.out.println("Painted: " + pos);
	}
	
	/*
	private class RotatedCard
	{
		private BufferedImage img;
		private int width;
		
		public RotatedCard(BufferedImage img, int width)
		{
			this.img = img;
			this.width = width;
		}
		
		public BufferedImage getImage()
		{
			return img;
		}
		
		public int getWidth()
		{
			return width;
		}
	}
	*/
	
	public static enum AnimationType
	{
		NORMAL, IMPORTANT
	}
}
