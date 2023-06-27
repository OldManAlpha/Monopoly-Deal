package oldmana.md.client.card;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardProperty extends Card
{
	private List<PropertyColor> colors;
	private boolean base;
	private boolean stealable;
	
	public CardProperty(int id, List<PropertyColor> colors, boolean base, boolean stealable, int value, String name)
	{
		super(id, value, name);
		this.colors = colors;
		this.base = base;
		this.stealable = stealable;
	}
	
	public boolean isSingleColor()
	{
		return colors.size() == 1;
	}
	
	public boolean isBiColor()
	{
		return colors.size() == 2;
	}
	
	public boolean isBase()
	{
		return base;
	}
	
	public void setBase(boolean base)
	{
		this.base = base;
	}
	
	public boolean isStealable()
	{
		return stealable;
	}
	
	public void setStealable(boolean stealable)
	{
		this.stealable = stealable;
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
	
	public void setColors(List<PropertyColor> colors)
	{
		this.colors = colors;
	}
	
	public static class PropertyColor
	{
		private static List<PropertyColor> colors = new ArrayList<PropertyColor>();
		private static Map<Integer, PropertyColor> idColorMap = new HashMap<Integer, PropertyColor>();
		
		private int id;
		
		private String name;
		private String label;
		
		private Color color;
		
		private boolean buildable;
		
		private byte[] rent;
		
		public PropertyColor() {}
		
		public static PropertyColor create(int id, String name, String label, Color color, boolean buildable, byte... rent)
		{
			PropertyColor theColor = fromID(id);
			if (theColor == null)
			{
				theColor = new PropertyColor();
				colors.add(theColor);
				idColorMap.put(id, theColor);
			}
			
			theColor.id = id;
			
			theColor.name = name;
			theColor.label = label;
			
			theColor.color = color;
			
			theColor.buildable = buildable;
			
			theColor.rent = rent;
			
			return theColor;
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
		
		public double getLuminance()
		{
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			
			return ((red * 0.2126) + (green * 0.7152) + (blue * 0.0722)) / 255;
		}
		
		public boolean isDark()
		{
			return getLuminance() < 0.2;
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
			return idColorMap.get(id);
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
			idColorMap.clear();
		}
	}
}
