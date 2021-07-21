package oldmana.md.client.gui.component;

import java.awt.Point;
import java.awt.image.BufferedImage;

import oldmana.md.client.card.Card;

public class MDMovingCardNew extends MDComponent
{
	private Card card;
	private boolean startFacing;
	private BufferedImage cache;
	
	private Point startPos;
	private Point endPos;
	private double startScale;
	private double endScale;
	private double speed;
	
	public double[] progMap;
	public volatile int pos;
	private BufferedImage[] frameCache;
	private volatile int framePos;
	
	private AnimationType animType = AnimationType.NORMAL;
	
	public MDMovingCardNew(Card card, boolean startFacing, Point startPos, double startScale, Point endPos, double endScale, double speed)
	{
		this.card = card;
		this.startFacing = startFacing;
		this.startPos = startPos;
		this.endPos = endPos;
		this.startScale = startScale;
		this.endScale = endScale;
		this.speed = speed;
	}
	
	private double getProgressBetween(double start, double end, double num)
	{
		return (num - start) / (end - start);
	}
	
	public class Card3D
	{
		private BufferedImage start;
		private BufferedImage end;
		
		public BufferedImage getTexture(double rotation)
		{
			return null;
		}
	}
	
	public static enum AnimationType
	{
		NORMAL, IMPORTANT
	}
}
