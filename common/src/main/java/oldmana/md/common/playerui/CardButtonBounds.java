package oldmana.md.common.playerui;

import oldmana.md.common.util.DataUtil;

public class CardButtonBounds
{
	public static final CardButtonBounds TOP = new CardButtonBounds(0.1, 0.2, 0.8, 0.3);
	public static final CardButtonBounds CENTER = new CardButtonBounds(0.1, 0.55, 0.8, 0.3);
	public static final CardButtonBounds BOTTOM = new CardButtonBounds(0.1, 0.9, 0.8, 0.3);
	public static final CardButtonBounds MOVE_START = new CardButtonBounds(0.02, 1.3, 0.24, 0.16);
	public static final CardButtonBounds MOVE_END = new CardButtonBounds(0.74, 1.3, 0.24, 0.16);
	public static final CardButtonBounds MOVE_LEFT = new CardButtonBounds(0.28, 1.3, 0.20, 0.16);
	public static final CardButtonBounds MOVE_RIGHT = new CardButtonBounds(0.52, 1.3, 0.20, 0.16);
	
	
	private double x;
	private double y;
	private double width;
	private double height;
	
	/**
	 * The bounds for the x-axis range from 0.0 to 1.0.<br>
	 * The bounds for the y-axis range from 0.0 to 1.5.
	 * @param x The x position of the button
	 * @param y The y position of the button
	 * @param width The width of the button
	 * @param height The height of the button
	 */
	public CardButtonBounds(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public CardButtonBounds(short x, short y, short width, short height)
	{
		this.x = DataUtil.convertShortToFraction(x);
		this.y = DataUtil.convertShortToFraction(y);
		this.width = DataUtil.convertShortToFraction(width);
		this.height = DataUtil.convertShortToFraction(height);
	}
	
	public double getX()
	{
		return x;
	}
	
	public short getEncodedX()
	{
		return DataUtil.convertFractionToShort(x);
	}
	
	public double getY()
	{
		return y;
	}
	
	public short getEncodedY()
	{
		return DataUtil.convertFractionToShort(y / 1.5);
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public short getEncodedWidth()
	{
		return DataUtil.convertFractionToShort(width);
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public short getEncodedHeight()
	{
		return DataUtil.convertFractionToShort(height / 1.5);
	}
}
