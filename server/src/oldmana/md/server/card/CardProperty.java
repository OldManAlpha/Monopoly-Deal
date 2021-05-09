package oldmana.md.server.card;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oldmana.general.mjnetworkingapi.packet.Packet;
import oldmana.md.net.packet.server.PacketCardPropertyData;

public class CardProperty extends Card
{
	private List<PropertyColor> colors;
	private boolean base;
	
	public CardProperty(PropertyColor color, int value, String name)
	{
		super(value, name);
		colors = new ArrayList<PropertyColor>(1);
		colors.add(color);
		base = true;
		setDefaultDescription();
	}
	
	public CardProperty(List<PropertyColor> colors, int value, String name, boolean base)
	{
		super(value, name);
		this.colors = colors;
		this.base = base;
		setDefaultDescription();
	}
	
	public CardProperty(PropertyColor[] colors, int value, String name, boolean base)
	{
		super(value, name);
		this.colors = Arrays.asList(colors);
		this.base = base;
		setDefaultDescription();
	}
	
	private void setDefaultDescription()
	{
		setDescription("Property cards can be played on your table. They are used to rent on and contribute towards winning the game. "
				+ "They can be used to pay rent, either by choice or forcibly if you do not have the money to pay the rent.");
	}
	
	public boolean isSingleColor()
	{
		return colors.size() == 1;
	}
	
	public boolean isBiColor()
	{
		return colors.size() == 2;
	}
	
	/**If a card is a base, it may be a color without having supporting properties
	 * 
	 * @return True if card is base
	 */
	public boolean isBase()
	{
		return base;
	}
	
	/**If there are multiple colors on the card, the first color is returned
	 * 
	 * @return First color of the card
	 */
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
	
	@Override
	public Packet getCardDataPacket()
	{
		byte[] types = new byte[colors.size()];
		for (int i = 0 ; i < types.length ; i++)
		{
			types[i] = colors.get(i).getID();
		}
		return new PacketCardPropertyData(getID(), getName(), getValue(), types, isBase(), getDescription().getID());
	}
	
	@Override
	public CardType getType()
	{
		return CardType.PROPERTY;
	}
	
	@Override
	public String toString()
	{
		String str = "CardProperty (" + colors.size() + " Color" + (colors.size() != 1 ? "s" : "") + ": ";
		for (PropertyColor color : colors)
		{
			str += color.getLabel() + "/";
		}
		str = str.substring(0, str.length() - 1);
		str += ") (Base: " + isBase() + ")";
		str += " (Name: " + getName() + ")";
		str += " (" + getValue() + "M)";
		return str;
	}
	
	
	public static List<CardProperty> getPropertyCards(int[] ids)
	{
		List<CardProperty> props = new ArrayList<CardProperty>();
		List<Card> cards = Card.getCards(ids);
		for (Card card : cards)
		{
			props.add((CardProperty) card);
		}
		return props;
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
		
		public static PropertyColor[] TIER_1 = new PropertyColor[] {BROWN, LIGHT_BLUE};
		public static PropertyColor[] TIER_2 = new PropertyColor[] {MAGENTA, ORANGE};
		public static PropertyColor[] TIER_3 = new PropertyColor[] {RED, YELLOW};
		public static PropertyColor[] TIER_4 = new PropertyColor[] {GREEN, DARK_BLUE};
		public static PropertyColor[] TIER_OTHER = new PropertyColor[] {RAILROAD, UTILITY};
		public static PropertyColor[][] TIERS = new PropertyColor[][] {TIER_1, TIER_2, TIER_3, TIER_4, TIER_OTHER};
		
		byte id;
		int[] rent;
		
		Color color;
		
		String label;
		
		String name = "";
		
		PropertyColor(int id, Color color, String label, int... rent)
		{
			this.id = (byte) id;
			this.rent = rent;
			
			this.color = color;
			
			this.label = label;
			
			String[] words = name().split("_");
			for (int i = 0 ; i < words.length ; i++)
			{
				String word = words[i].toLowerCase();
				name += Character.toUpperCase(word.charAt(0)) + word.substring(1);
				if (i + 1 < words.length)
				{
					name += " ";
				}
			}
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
		
		public String getFriendlyName()
		{
			return name;
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
