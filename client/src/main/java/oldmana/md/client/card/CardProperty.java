package oldmana.md.client.card;

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
	
	public CardProperty(int id, List<PropertyColor> colors, boolean base, int value, String name, CardDescription description)
	{
		super(id, value, name);
		this.colors = colors;
		this.base = base;
		setDescription(description);
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
	
	public static class PropertyColor
	{
		private static List<PropertyColor> colors = new ArrayList<PropertyColor>();
		
		
		private int id;
		
		private String name;
		private String label;
		
		private Color color;
		
		private boolean buildable;
		
		private byte[] rent;
		
		
		public PropertyColor(int id, String name, String label, Color color, boolean buildable, byte... rent)
		{
			this.id = id;
			
			this.name = name;
			this.label = label;
			
			this.color = color;
			
			this.buildable = buildable;
			
			this.rent = rent;
			
			colors.add(this);
		}
		
		public int getRent(int propertyCount)
		{
			return rent[propertyCount - 1];
		}
		
		public int getMaxProperties()
		{
			return rent.length;
		}
		
		public boolean isBuildable()
		{
			return buildable;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		public byte getID()
		{
			return (byte) id;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public static PropertyColor fromID(int id)
		{
			for (PropertyColor color : getAllColors())
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
		
		public static List<PropertyColor> getAllColors()
		{
			return new ArrayList<PropertyColor>(colors);
		}
		
		public static void clearColors()
		{
			colors.clear();
		}
	}
}
