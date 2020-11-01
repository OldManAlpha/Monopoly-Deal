package oldmana.md.client.card;

import oldmana.md.client.card.CardProperty.PropertyColor;

public class CardActionRent extends CardAction
{
	private PropertyColor[] colors;
	
	public CardActionRent(int id, int value, PropertyColor... colors)
	{
		super(id, value, "RENT");
		this.colors = colors;
	}
	
	public PropertyColor[] getRentColors()
	{
		return colors;
	}
	
	@Override
	public String toString()
	{
		String str = "CardActionRent: " + colors.length + ": ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str.substring(0, str.length() - 1);
		str += " (" + getValue() + "M)";
		return str;
	}
}
