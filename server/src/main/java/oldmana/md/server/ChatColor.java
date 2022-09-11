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
	RED(PropertyColor.RED.getColor()),
	YELLOW(PropertyColor.YELLOW.getColor()),
	GREEN(PropertyColor.GREEN.getColor()),
	DARK_BLUE(PropertyColor.DARK_BLUE.getColor()),
	UTILITY(PropertyColor.UTILITY.getColor()),
	LIGHT_GREEN(new Color(144,238,144)),
	LIGHT_GRAY(Color.LIGHT_GRAY),
	GRAY(Color.GRAY),
	DARK_GRAY(Color.DARK_GRAY),
	WHITE(Color.WHITE),
	LINK(new Color(173, 216, 230));
	
	public static String PREFIX_ALERT = "[" + ChatColor.RED + "!" + ChatColor.WHITE + "] ";
	
	String value;
	
	ChatColor(Color color)
	{
		value = toChatColor(color);
	}
	
	@Override
	public String toString()
	{
		return value;
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
