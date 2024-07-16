package oldmana.md.common.playerui;

import java.awt.Color;

public class InfoPlateBase
{
	private int id;
	
	private int priority = 100;
	
	private String text = "";
	private Color textColor;
	private Color color;
	private Color borderColor;
	
	public InfoPlateBase(int id, String text, Color textColor, Color color, Color borderColor)
	{
		this.id = id;
		this.text = text;
		this.textColor = textColor;
		this.color = color;
		this.borderColor = borderColor;
	}
	
	public InfoPlateBase(int id, int priority, String text, Color textColor, Color color, Color borderColor)
	{
		this(id, text, textColor, color, borderColor);
		this.priority = priority;
	}
	
	public InfoPlateBase() {}
	
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * The higher the priority value, the further left the plate will be relative to other plates. Default priority is 100.
	 * @param priority The priority
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getBorderColor()
	{
		return borderColor;
	}
	
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}
}
