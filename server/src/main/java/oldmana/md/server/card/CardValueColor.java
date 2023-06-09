package oldmana.md.server.card;

import java.awt.Color;

public enum CardValueColor
{
	ZERO(new Color(230, 230, 230)),
	ONE(new Color(243, 237, 159)),
	TWO(new Color(237, 209, 178)),
	THREE(new Color(230, 242, 203)),
	FOUR(new Color(194, 224, 233)),
	FIVE(new Color(193, 161, 203)),
	TEN(new Color(247, 210, 82)),
	GREATER_THAN_TEN(new Color(132, 240, 255)),
	OTHER(new Color(225, 170, 160));
	
	private final Color color;
	
	CardValueColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public static CardValueColor getByValue(int value)
	{
		switch (value)
		{
			case 0:
				return ZERO;
			case 1:
				return ONE;
			case 2:
				return TWO;
			case 3:
				return THREE;
			case 4:
				return FOUR;
			case 5:
				return FIVE;
			case 10:
				return TEN;
			default:
			{
				if (value > 10)
				{
					return GREATER_THAN_TEN;
				}
				else if (value > 5)
				{
					return OTHER;
				}
			}
		}
		return OTHER;
	}
}
