package oldmana.md.client.card;

import oldmana.md.client.card.CardProperty.PropertyColor;

public class CardActionRent extends CardAction
{
	private PropertyColor[] colors;
	
	public CardActionRent(int id, int value, String name)
	{
		super(id, value, name);
	}
	
	public PropertyColor[] getRentColors()
	{
		return colors;
	}
	
	public void setRentColors(PropertyColor[] colors)
	{
		this.colors = colors;
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
