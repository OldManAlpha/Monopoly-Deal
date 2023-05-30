package oldmana.md.server;

import java.awt.Color;
import java.nio.ByteBuffer;

import oldmana.md.server.card.PropertyColor;

public enum ChatColor
{
	BROWN(PropertyColor.BROWN.getColor()),
	LIGHT_BLUE(PropertyColor.LIGHT_BLUE.getColor()),
	MAGENTA(PropertyColor.MAGENTA.getColor()),
	ORANGE(PropertyColor.ORANGE.getColor()),
	LIGHT_ORANGE(new Color(235, 170, 3)),
	LIGHT_RED(new Color(255, 120, 120)),
	RED(PropertyColor.RED.getColor()),
	LIGHT_YELLOW(new Color(255, 255, 120)),
	YELLOW(PropertyColor.YELLOW.getColor()),
	GREEN(PropertyColor.GREEN.getColor()),
	DARK_BLUE(PropertyColor.DARK_BLUE.getColor()),
	RAILROAD(PropertyColor.RAILROAD.getColor()),
	UTILITY(PropertyColor.UTILITY.getColor()),
	LIGHT_GREEN(new Color(144,238,144)),
	WHITE(Color.WHITE),
	FAINTLY_GRAY(new Color(220, 220, 220)),
	LIGHT_GRAY(Color.LIGHT_GRAY),
	GRAY(Color.GRAY),
	DARK_GRAY(Color.DARK_GRAY),
	BLACK(Color.BLACK),
	LINK(new Color(173, 216, 230));
	
	public static final String PREFIX_ALERT = "[" + ChatColor.RED + "!" + ChatColor.WHITE + "] ";
	
	private final Color color;
	private final String value;
	
	ChatColor(Color color)
	{
		this.color = color;
		value = toChatColor(color);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	@Override
	public String toString()
	{
		return value;
	}
	
	public static String toHexColor(Color color)
	{
		String buf = Integer.toHexString(color.getRGB());
		return buf.substring(buf.length() - 6);
	}
	
	public static String toChatColor(Color color)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(color.getRGB());
		buffer.position(0);
		return "§1" + buffer.getChar() + buffer.getChar();
	}
	
	public static String stripColors(String str)
	{
		StringBuilder sb = new StringBuilder(str);
		int index;
		while ((index = sb.indexOf("§1")) != -1)
		{
			sb.replace(index, index + 4, "");
		}
		return sb.toString();
	}
	
	public static String stripLinks(String str)
	{
		StringBuilder sb = new StringBuilder(str);
		int index;
		while ((index = sb.indexOf("§2")) != -1)
		{
			sb.replace(index, index + 5, "");
		}
		return sb.toString();
	}
	
	public static String stripFormatting(String str)
	{
		return stripLinks(stripColors(str));
	}
	
	public static String of(int r, int g, int b)
	{
		return toChatColor(new Color(r, g, b));
	}
}
