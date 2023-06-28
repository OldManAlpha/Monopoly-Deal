package oldmana.md.client.gui.component;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import oldmana.md.client.Scheduler;
import oldmana.md.client.card.Card;
import oldmana.md.client.gui.screen.TableScreen;
import oldmana.md.client.gui.util.GraphicsUtils;
import oldmana.md.common.card.CardAnimationType;

import javax.swing.SwingUtilities;

public class MDMovingCard extends MDComponent
{
	private static ExecutorService renderingExecutor = Executors.newFixedThreadPool(2);
	private static Executor edtExecutor = SwingUtilities::invokeLater;
	private static Executor fxExecutor = Platform::runLater;
	
	
	private Card start;
	private BufferedImage startCache;
	private Card end;
	private BufferedImage endCache;
	// Optional
	public Card flash;
	public BufferedImage flashCache;
	
	private Point startPos;
	private Supplier<Point> endPosSupplier;
	private double startScale;
	private double endScale;
	private double time;
	
	public int frames;
	public int pos;
	public double[] animMap;
	public double[] posMap;
	public Rectangle[] sizeLocMap;
	private BufferedImage[] frameCache;
	private int latestCached;
	private BufferedImage latestCache;
	private int nextCache;
	
	private int borderWidth;
	
	private CardAnimationType animType;
	
	public MDMovingCard(Card card, Point startPos, double startScale, Supplier<Point> endPosSupplier, double endScale, double time)
	{
		this(card, startPos, startScale, card, endPosSupplier, endScale, time);
	}
	
	public MDMovingCard(Card start, Point startPos, double startScale, Card end, Supplier<Point> endPosSupplier, double endScale, double time)
	{
		this(start, startPos, startScale, end, endPosSupplier, endScale, time, CardAnimationType.NORMAL);
	}
	
	public MDMovingCard(Card start, Point startPos, double startScale, Card end, Supplier<Point> endPosSupplier, double endScale, double time,
	                    CardAnimationType animType)
	{
		this(start, startPos, startScale, end, endPosSupplier, endScale, time, animType, null);
	}
	
	public MDMovingCard(Card start, Point startPos, double startScale, Card end, Supplier<Point> endPosSupplier, double endScale, double time,
	                    CardAnimationType animType, Card flash)
	{
		this.startPos = startPos;
		this.endPosSupplier = endPosSupplier;
		this.startScale = startScale;
		this.endScale = endScale;
		this.start = start;
		this.end = end;
		this.time = time;
		this.animType = animType;
		
		startPos.setLocation(startPos.getX() + (GraphicsUtils.getCardWidth(startScale) / 2),
				startPos.getY() + (GraphicsUtils.getCardHeight(startScale) / 2));
		
		double largestScale = getLargestScale();
		int largestWidth = getLargestWidth();
		int largestHeight = getLargestHeight();
		
		startCache = GraphicsUtils.createImage(largestWidth, largestHeight);
		Graphics2D cardGr = startCache.createGraphics();
		//cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cardGr.drawImage(start != null ? start.getGraphics(largestScale * getScale()) :
				Card.getBackGraphics(largestScale * getScale()), 0, 0/*, largestWidth, largestHeight*/, null);
		
		endCache = GraphicsUtils.createImage(largestWidth, largestHeight);
		cardGr = endCache.createGraphics();
		//cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cardGr.drawImage(end != null ? end.getGraphics(largestScale * getScale()) :
				Card.getBackGraphics(largestScale * getScale()), 0, 0/*, largestWidth, largestHeight*/, null);
		
		if (flash != null)
		{
			setFlash(flash);
		}
		
		frames = (int) Math.max(Math.round(Scheduler.getFPS() * time), 1);
		
		frameCache = new BufferedImage[frames];
		posMap = new double[frames];
		animMap = new double[frames];
		sizeLocMap = new Rectangle[frames];
		//int largestMovement = 1;
		for (int i = 0 ; i < frames ; i++)
		{
			double pos = i / (double) frames;
			posMap[i] = (1.6 * pos) + (-0.7 * Math.pow(pos, 2)) + (0.1 * Math.pow(pos, 3));
			animMap[i] = Math.min(posMap[i] * 1.2, 1);
			sizeLocMap[i] = getSizeLoc(posMap[i]);
			//if (i != 0)
			//{
			//	int xDiff = Math.max(sizeLocMap[i].x, sizeLocMap[i - 1].x) - Math.min(sizeLocMap[i].x, sizeLocMap[i - 1].x);
			//	int yDiff = Math.max(sizeLocMap[i].y, sizeLocMap[i - 1].y) - Math.min(sizeLocMap[i].y, sizeLocMap[i - 1].y);
			//	largestMovement = Math.max(largestMovement, Math.max(xDiff, yDiff));
			//}
		}
		//borderWidth = largestMovement;
		borderWidth = scale(15);
		
		pos = -1;
		tickMove();
		
		for (int i = 0 ; i < Math.min(10, frames - 1) ; i++)
		{
			renderNextFrame();
		}
	}
	
	public void setFlash(Card card)
	{
		flash = card;
		flashCache = GraphicsUtils.createImage(getLargestWidth(), getLargestHeight());
		Graphics2D cardGr = flashCache.createGraphics();
		cardGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		cardGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		cardGr.drawImage(flash.getGraphics(getLargestScale() * getScale()), 0, 0, getLargestWidth(), getLargestHeight(), null);
	}
	
	private Rectangle getSizeLoc(double posProg)
	{
		int cardWidth = GraphicsUtils.getCardWidth();
		int cardHeight = GraphicsUtils.getCardHeight();
		
		Rectangle rec = new Rectangle();
		
		Point endPos = endPosSupplier.get();
		endPos.setLocation(endPos.getX() + (GraphicsUtils.getCardWidth(endScale) / 2),
				endPos.getY() + (GraphicsUtils.getCardHeight(endScale) / 2));
		if (animType == CardAnimationType.NORMAL)
		{
			double scale = (startScale * 1) + ((endScale - startScale) * posProg);
			rec.setSize((int) Math.round(scale * cardWidth), (int) Math.round(scale * cardHeight));
			rec.setLocation((int) Math.round(startPos.x + ((endPos.x - startPos.x) * posProg)),
					(int) Math.round(startPos.y + ((endPos.y - startPos.y) * posProg)));
		}
		else if (animType == CardAnimationType.IMPORTANT)
		{
			TableScreen screen = getClient().getTableScreen();
			// 0 - 0.25: Moving To Front
			// 0.25 - 0.75: Front
			// 0.8 - 1: Moving To Destination
			if (posProg < 0.25)
			{
				double scale = (startScale * 1) + ((4 - startScale) * getProgressBetween(0, 0.25, posProg));
				rec.setSize((int) Math.round(scale * cardWidth), (int) Math.round(scale * cardHeight));
				int xPos = (screen.getWidth() / 2) + scale(25);
				int yPos = ((screen.getHeight() - getClient().getThePlayer().getHand().getUI().getHeight()) / 2);
				rec.setLocation((int) Math.round(startPos.x + ((xPos - startPos.x) * getProgressBetween(0, 0.25, posProg))),
						(int) Math.round(startPos.y + ((yPos - startPos.y) * getProgressBetween(0, 0.25, posProg))));
			}
			else if (posProg > 0.8)
			{
				double progBetween = getProgressBetween(0.8, 1, posProg);
				double scale = (4) + ((endScale - 4) * progBetween);
				rec.setSize((int) Math.round(scale * cardWidth), (int) Math.round(scale * cardHeight));
				int xPos = (screen.getWidth() / 2) - scale(25);
				int yPos = ((screen.getHeight() - getClient().getThePlayer().getHand().getUI().getHeight()) / 2);
				progBetween = (0.7 * progBetween) + (-0.2 * Math.pow(progBetween, 2)) + (0.5 * Math.pow(progBetween, 3));
				rec.setLocation((int) Math.round(xPos + ((endPos.x - xPos) * progBetween)),
						(int) Math.round(yPos + ((endPos.y - yPos) * progBetween)));
			}
			else
			{
				int xPos = (screen.getWidth() / 2) + scale(25) - (int) (getProgressBetween(0.25, 0.8, posProg) * (scale(25) * 2));
				int yPos = ((screen.getHeight() - getClient().getThePlayer().getHand().getUI().getHeight()) / 2);
				rec.setSize(Math.round(4 * cardWidth), Math.round(4 * cardHeight));
				rec.setLocation(Math.round(xPos), Math.round(yPos));
			}
		}
		
		return rec;
	}
	
	public boolean tickMove()
	{
		if (latestCached < pos && frameCache[pos] != null)
		{
			latestCache = frameCache[pos];
			latestCached = pos;
		}
		if (pos > 0 && frames > pos - 1)
		{
			frameCache[pos] = null;
		}
		pos++;
		
		if (pos == frames)
		{
			return true;
		}
		
		Rectangle loc = sizeLocMap[pos];
		setSize(loc.getWidth() + (getBorderWidth() * 2), loc.getHeight() + (getBorderWidth() * 2));
		//setLocationCentered(loc.getX(), loc.getY());
		Rectangle rec = getSizeLoc(posMap[pos]);
		setLocationCentered(rec.getX(), rec.getY());
		
		if (nextCache < Math.min(pos + 10, frames))
		{
			renderNextFrame();
		}
		repaint();
		return pos == frames;
	}
	
	public int getFrames()
	{
		return frames;
	}
	
	public double getCurrentPosition()
	{
		return pos + 1 < posMap.length ? posMap[pos] : 1;
	}
	
	public int getBorderWidth()
	{
		return borderWidth;
	}
	
	private double getProgressBetween(double start, double end, double num)
	{
		return (num - start) / (end - start);
	}
	
	public double getLargestScale()
	{
		if (animType == CardAnimationType.IMPORTANT)
		{
			return 4;
		}
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
	
	public boolean isMystery()
	{
		return start == null && end == null && flash == null;
	}
	
	public boolean isFlash()
	{
		return start == null && end == null && flash != null;
	}
	
	public BufferedImage getFace(double rotation)
	{
		//BufferedImage image = GraphicsUtils.createImage(getLargestWidth(), getLargestHeight());
		//Graphics2D g = image.createGraphics();
		boolean face = rotation > 180; // True = Start Card, False = End Card
		if (flash != null && !face) // We're flashing and it's the "end" card
		{
			return GraphicsUtils.createCopy(flashCache);
			//g.drawImage(flashCache, 0, 0, null);
			//return image;
		}
		else if (isMystery() && !face) // The card is a mystery and it's the "end" card
		{
			return Card.getMysteryGraphics(getLargestScale() * 2);
		}
		return GraphicsUtils.createCopy(start == end && !face ? Card.getBackGraphics(getLargestScale()) : face ? startCache : endCache);
		//g.drawImage(start == end && !face ? Card.getBackGraphics(getLargestScale()) : face ? startCache : endCache, 0, 0, null);
		//return image;
	}
	
	public BufferedImage getFaceNoCopy(double rotation)
	{
		boolean face = rotation > 180; // True = Start Card, False = End Card
		if (flash != null && !face) // We're flashing and it's the "end" card
		{
			return flashCache;
		}
		else if (isMystery() && !face) // The card is a mystery and it's the "end" card
		{
			return Card.getMysteryGraphics(getLargestScale() * 2);
		}
		return start == end && !face ? Card.getBackGraphics(getLargestScale()) : face ? startCache : endCache;
	}
	
	public void shine(Graphics2D g, int width, int height, double pos)
	{
		width *= 1.3333;
		height *= 1.3333;
		int widthPos = (int) (pos * width) - (width / 4);
		int heightPos = (int) (pos * height) - (height / 4);
		java.awt.Color transparent = new java.awt.Color(255, 255, 255, 0);
		java.awt.Color shine = new java.awt.Color(255, 255, 255, 170);
		LinearGradientPaint paint = new LinearGradientPaint(widthPos, heightPos, widthPos + (width / 4), heightPos + (height / 4),
				new float[] {0F, 0.45F, 0.55F, 1F}, new java.awt.Color[] {transparent, shine, shine, transparent});
		g.setPaint(paint);
		g.fillRect(-(width / 4), -(height / 4), width, height);
	}
	
	public CompletableFuture<BufferedImage> renderFrame(double rotation, double shineProg, int frame)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			if (pos > frame) // We're behind, so we should just move to render a more recent frame instead
			{
				System.out.println("Skipping Frame " + frame + " because we're at " + pos);
				return null;
			}
			
			double degrees = rotation - 90;
			degrees = (degrees + 360) % 360;
			double angle = Math.toRadians(degrees - (start != null ? 180 : 0));
			BufferedImage image = downsize(getFaceNoCopy(degrees), (int) sizeLocMap[frame].getWidth(), (int) sizeLocMap[frame].getHeight());
			int width = image.getWidth();
			int height = image.getHeight();
			if (shineProg > 0)
			{
				Graphics2D g = image.createGraphics();
				shine(g, width, height, shineProg);
				g.dispose();
			}
			
			if (rotation == 0)
			{
				return image;// downsize(image, (int) sizeLocMap[frame].getWidth(), (int) sizeLocMap[frame].getHeight());
			}
			
			//Canvas canvas = new Canvas(width, height);
			//GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
			ImageView imageView = new ImageView(SwingFXUtils.toFXImage(image, null));
			
			PerspectiveTransform trans = new PerspectiveTransform();
			
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
			
			imageView.setEffect(trans);
			
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			
			Image newImage;
			try
			{
				newImage = CompletableFuture.supplyAsync(() -> imageView.snapshot(params, null), fxExecutor).get();
			}
			catch (Exception e)
			{
				System.err.println("Exception occurred while rendering rotated card!");
				e.printStackTrace();
				return null;
			}
			//graphicsContext.drawImage(newImage, 0, 0);
			
			BufferedImage from = SwingFXUtils.fromFXImage(newImage, image);
			BufferedImage rendered = GraphicsUtils.createImage(width, height);
			Graphics2D g = rendered.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(from, (width / 2) - (drawWidth / 2), -1, drawWidth, height + 1, null);
			//System.out.println("Almost done with " + frame + " on " + Thread.currentThread().getName());
			return rendered;// downsize(rendered, (int) sizeLocMap[frame].getWidth(), (int) sizeLocMap[frame].getHeight());
		}, renderingExecutor);
	}
	
	private BufferedImage downsize(BufferedImage img, int width, int height)
	{
		BufferedImage render = GraphicsUtils.createImage(width, height);
		Graphics2D g = render.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, width, height, null);
		return render;
	}
	
	public void renderNextFrame()
	{
		final int frame = nextCache++;
		double shine = 0;
		double degrees = 0;
		
		if (animType == CardAnimationType.NORMAL)
		{
			degrees = start != end ? (animMap[frame] * 180) : (isFlash() ? (animMap[frame] * 360) : 0);
			if (start != end && end != null && animMap[frame] > 0.5)
			{
				shine = (animMap[frame] - 0.5) * 2;
			}
		}
		else if (animType == CardAnimationType.IMPORTANT)
		{
			double posProg = posMap[frame];
			if (posProg < 0.25)
			{
				degrees = getProgressBetween(0, 0.25, posProg) * (360 + 180 - 40 + (start == null ? 0 : 180));
			}
			else if (posProg > 0.8)
			{
				degrees = (start != null ? 0 : 180) + 40 + (getProgressBetween(0.8, 1, posProg) * ((end == null ? 180 : 0) - 40));
			}
			else
			{
				degrees = (start != null ? 0 : 180) - 40 + (getProgressBetween(0.25, 0.8, posProg) * 80);
				shine = getProgressBetween(0.25, 0.8, posProg);
			}
		}
		
		renderFrame(degrees, shine, frame).thenAcceptAsync(img ->
		{
			if (img == null)
			{
				System.out.println("Failed to render " + frame);
				return;
			}
			frameCache[frame] = img;
			if (frame > latestCached && frame < pos)
			{
				latestCache = img;
				latestCached = frame;
			}
		}, edtExecutor);
	}
	
	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		int pos = Math.min(this.pos, frames - 1);
		
		//System.out.println("Painting: " + pos);
		
		// Get the most recently rendered image
		BufferedImage result = getPaintableFrame(pos);
		//for (int i = 4 ; i >= 0 ; i--)
		//{
		//	paintPhantom(g, pos, i, 5);
		//}
		g.drawImage(result, getBorderWidth(), getBorderWidth(), null);
	}
	
	private void paintPhantom(Graphics2D g, int pos, int frame, int frames)
	{
		if (pos == 0)
		{
			return;
		}
		double framePos = frame / (double) frames;
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (float) framePos));
		g.drawImage(getPaintableFrame(pos - 1), getBorderWidth() - (int) ((sizeLocMap[pos].x - sizeLocMap[pos - 1].x) * framePos),
				getBorderWidth() - (int) ((sizeLocMap[pos].y - sizeLocMap[pos - 1].y) * framePos), null);
		g.setComposite(c);
	}
	
	private BufferedImage getPaintableFrame(int pos)
	{
		BufferedImage result = frameCache[pos] != null ? frameCache[pos] : latestCache;
		if (result == null) // Fallback to static image
		{
			result = downsize(startCache, (int) sizeLocMap[pos].getWidth(), (int) sizeLocMap[pos].getHeight());
		}
		return result;
	}
}
