package oldmana.general.md.client.card;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CardProperty extends Card
{
	private List<PropertyColor> colors;
	private boolean base;
	
	public CardProperty(int id, PropertyColor color, int value, String name)
	{
		super(id, value, name);
		colors = new ArrayList<PropertyColor>();
		colors.add(color);
		base = true;
	}
	
	public CardProperty(int id, List<PropertyColor> colors, boolean base, int value, String name)
	{
		super(id, value, name);
		this.colors = colors;
		this.base = base;
	}
	
	public boolean isSingleColor()
	{
		return colors.size() == 1;
	}
	
	public boolean isBiColor()
	{
		return colors.size() == 2;
	}
	
	public boolean isPropertyWildCard()
	{
		return colors.size() == 10;
	}
	
	public boolean isBase()
	{
		return base;
	}
	
	public PropertyColor getColor()
	{
		return colors.get(0);
	}
	
	public List<PropertyColor> getColors()
	{
		return colors;
	}
	
	public boolean hasColor(PropertyColor color)
	{
		return colors.contains(color);
	}
	
	public static enum PropertyColor
	{
		BROWN(0, new Color(134, 70, 27), "B", 1, 2),
		LIGHT_BLUE(1, new Color(187, 222, 241), "LB", 1, 2, 3),
		MAGENTA(2, new Color(189, 47, 131), "M", 1, 2, 4),
		ORANGE(3, new Color(227, 139, 3), "O", 1, 3, 5),
		RED(4, new Color(215, 16, 37), "R", 2, 3, 6),
		YELLOW(5, new Color(249, 239, 4), "Y", 2, 4, 6),
		GREEN(6, new Color(80, 180, 47), "G", 2, 4, 7),
		DARK_BLUE(7, new Color(64, 92, 165), "DB", 3, 8),
		RAILROAD(8, new Color(17, 17, 14), "RR", 1, 2, 3, 4),
		UTILITY(9, new Color(206, 229, 183), "U", 1, 2);
		
		byte id;
		int[] rent;
		
		Color color;
		
		String label;
		
		PropertyColor(int id, Color color, String label, int... rent)
		{
			this.id = (byte) id;
			this.rent = rent;
			
			this.color = color;
			
			this.label = label;
		}
		
		public int getRent(int propertyCount)
		{
			return rent[propertyCount - 1];
		}
		
		public int getMaxProperties()
		{
			return rent.length;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		public byte getID()
		{
			return id;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public static PropertyColor fromID(int id)
		{
			for (PropertyColor color : values())
			{
				if (color.getID() == id)
				{
					return color;
				}
			}
			return null;
		}
		
		public static List<PropertyColor> fromIDs(byte[] ids)
		{
			List<PropertyColor> colors = new ArrayList<PropertyColor>(ids.length);
			for (byte id : ids)
			{
				colors.add(fromID(id));
			}
			return colors;
		}
	}
}
