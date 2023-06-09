package oldmana.md.common.util;

import java.awt.Color;

public class ColorUtil
{
	public static Color fromRGBHex(String hex)
	{
		return new Color(Integer.parseUnsignedInt(hex, 16));
	}
	
	public static String toRGBHex(Color color)
	{
		String buf = Integer.toHexString(color.getRGB());
		return buf.substring(buf.length() - 6).toUpperCase();
	}
}
