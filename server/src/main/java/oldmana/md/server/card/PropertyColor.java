package oldmana.md.server.card;

import oldmana.md.common.net.api.packet.Packet;
import oldmana.md.common.net.packet.server.PacketPropertyColors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyColor
{
	private static int nextID = 0;
	private static final List<PropertyColor> colors = new ArrayList<PropertyColor>();
	private static final Map<String, PropertyColor> nameColorMap = new HashMap<String, PropertyColor>();
	private static final Map<String, PropertyColor> labelColorMap = new HashMap<String, PropertyColor>();
	private static final Map<Integer, PropertyColor> idColorMap = new HashMap<Integer, PropertyColor>();
	
	
	public static final PropertyColor BROWN = new PropertyColor("Brown", "B", new Color(134, 70, 27), true, 1, 2);
	public static final PropertyColor LIGHT_BLUE = new PropertyColor("Light Blue", "LB", new Color(187, 222, 241), true, 1, 2, 3);
	public static final PropertyColor MAGENTA = new PropertyColor("Magenta", "M", new Color(189, 47, 131), true, 1, 2, 4);
	public static final PropertyColor ORANGE = new PropertyColor("Orange", "O", new Color(227, 139, 3), true, 1, 3, 5);
	public static final PropertyColor RED = new PropertyColor("Red", "R", new Color(215, 16, 37), true, 2, 3, 6);
	public static final PropertyColor YELLOW = new PropertyColor("Yellow", "Y", new Color(249, 239, 4), true, 2, 4, 6);
	public static final PropertyColor GREEN = new PropertyColor("Green", "G", new Color(80, 180, 47), true, 2, 4, 7);
	public static final PropertyColor DARK_BLUE = new PropertyColor("Dark Blue", "DB", new Color(64, 92, 165), true, 3, 8);
	public static final PropertyColor RAILROAD = new PropertyColor("Railroad", "RR", new Color(17, 17, 14), false, 1, 2, 3, 4);
	public static final PropertyColor UTILITY = new PropertyColor("Utility", "U", new Color(206, 229, 183), false, 1, 2);
	
	public static final PropertyColor[] TIER_1 = new PropertyColor[] {BROWN, LIGHT_BLUE};
	public static final PropertyColor[] TIER_2 = new PropertyColor[] {MAGENTA, ORANGE};
	public static final PropertyColor[] TIER_3 = new PropertyColor[] {RED, YELLOW};
	public static final PropertyColor[] TIER_4 = new PropertyColor[] {GREEN, DARK_BLUE};
	public static final PropertyColor[] TIER_OTHER = new PropertyColor[] {RAILROAD, UTILITY};
	public static final PropertyColor[][] TIERS = new PropertyColor[][] {TIER_1, TIER_2, TIER_3, TIER_4, TIER_OTHER};
	
	private static final List<PropertyColor> vanillaColors = new ArrayList<PropertyColor>(colors);
	
	
	private int id;
	
	private String name;
	private String label;
	
	private Color color;
	
	private boolean buildable;
	
	private int[] rent;
	
	
	/**
	 * Invoking this constructor automatically registers the color for use by the server. Clients joined before
	 * registering a color will not know of the color. Any clients already joined should be refreshed after a color is
	 * registered.
	 * <br><br>
	 * In mods, it is highly recommended to register colors during the loading process.
	 *
	 * @param name Name of the property color
	 * @param label Abbreviation of the name
	 * @param color The RGB color
	 * @param buildable Whether buildings can be placed on property sets of this color
	 * @param rent Rent values for the amount of properties with the color you have; The amount of values indicates the max set size
	 */
	public PropertyColor(String name, String label, Color color, boolean buildable, int... rent)
	{
		if (nameColorMap.containsKey(name))
		{
			throw new IllegalArgumentException("A color is already registered by the name '" + name + "'");
		}
		if (labelColorMap.containsKey(label))
		{
			throw new IllegalArgumentException("A color is already registered with the label '" + label + "'");
		}
		
		this.id = nextID++;
		
		this.name = name;
		this.label = label;
		
		this.color = color;
		
		this.buildable = buildable;
		
		this.rent = rent;
		
		colors.add(this);
		nameColorMap.put(name, this);
		labelColorMap.put(label, this);
		idColorMap.put(id, this);
	}
	
	public int getRent(int propertyCount)
	{
		return rent[propertyCount - 1];
	}
	
	public int[] getRents()
	{
		return rent;
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
	
	public static PropertyColor fromName(String name)
	{
		return nameColorMap.get(name);
	}
	
	public static PropertyColor fromLabel(String label)
	{
		return labelColorMap.get(label);
	}
	
	public static List<PropertyColor> getVanillaColors()
	{
		return new ArrayList<PropertyColor>(vanillaColors);
	}
	
	public static List<PropertyColor> getAllColors()
	{
		return new ArrayList<PropertyColor>(colors);
	}
	
	public static Packet getColorsPacket()
	{
		PacketPropertyColors packet = new PacketPropertyColors();
		int len = colors.size();
		packet.name = new String[len];
		packet.label = new String[len];
		packet.color = new int[len];
		packet.buildable = new boolean[len];
		packet.rents = new byte[len][];
		for (int i = 0 ; i < len ; i++)
		{
			PropertyColor color = colors.get(i);
			packet.name[i] = color.getName();
			packet.label[i] = color.getLabel();
			packet.color[i] = color.getColor().getRGB();
			packet.buildable[i] = color.isBuildable();
			int[] intRents = color.getRents();
			byte[] rents = new byte[intRents.length];
			for (int r = 0 ; r < intRents.length ; r++)
			{
				rents[r] = (byte) intRents[r];
			}
			packet.rents[i] = rents;
		}
		return packet;
	}
	
	/**
	 * <b>All currently joined players should be refreshed after this is called!</b>
	 * <br><br>
	 * Unregisters the color. A color should NEVER be unregistered if any registered cards are using the color.
	 * @param color The color to unregister
	 */
	public static void unregister(PropertyColor color)
	{
		colors.remove(color);
		nameColorMap.remove(color.getName());
		labelColorMap.remove(color.getLabel());
		idColorMap.remove((int) color.getID());
		// Reclaim IDs, if we can
		for (int i = nextID - 1 ; i >= 0 ; i--)
		{
			if (idColorMap.containsKey(i))
			{
				break;
			}
			nextID--;
		}
	}
}
