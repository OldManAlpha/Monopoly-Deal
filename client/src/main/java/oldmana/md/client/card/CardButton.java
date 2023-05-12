package oldmana.md.client.card;

import oldmana.md.common.playerui.ButtonColorScheme;

public class CardButton
{
	private String text;
	private CardButtonPosition position;
	private CardButtonType type;
	private ButtonColorScheme colors;
	
	public CardButton(String text, CardButtonPosition position, CardButtonType type, ButtonColorScheme colors)
	{
		this.text = text;
		this.position = position;
		this.type = type;
		this.colors = colors;
	}
	
	public String getText()
	{
		return text;
	}
	
	public CardButtonPosition getPosition()
	{
		return position;
	}
	
	public CardButtonType getType()
	{
		return type;
	}
	
	public ButtonColorScheme getColors()
	{
		return colors;
	}
	
	public enum CardButtonType
	{
		NORMAL(0), PROPERTY(1), ACTION_COUNTER(2), BUILDING(3);
		
		private final int id;
		
		CardButtonType(int id)
		{
			this.id = id;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
		
		public static CardButtonType fromID(int id)
		{
			for (CardButtonType type : values())
			{
				if (type.getID() == id)
				{
					return type;
				}
			}
			return null;
		}
	}
	
	public enum CardButtonPosition
	{
		TOP(1, 0.25),
		CENTER(2, 0.5),
		BOTTOM(3, 0.75);
		
		private int id;
		private double loc;
		
		CardButtonPosition(int id, double loc)
		{
			this.id = id;
			this.loc = loc;
		}
		
		public int getID()
		{
			return id;
		}
		
		public double getLocation()
		{
			return loc;
		}
		
		public static CardButtonPosition fromID(int id)
		{
			for (CardButtonPosition pos : values())
			{
				if (pos.getID() == id)
				{
					return pos;
				}
			}
			return null;
		}
	}
}
