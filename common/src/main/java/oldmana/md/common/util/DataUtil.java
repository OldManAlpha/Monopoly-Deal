package oldmana.md.common.util;

public class DataUtil
{
	/**
	 * Converts a double within the range of 0 to 1 to a short representation.
	 * @param number The number to compress to a short
	 * @return A short that represents the double
	 */
	public static short convertFractionToShort(double number)
	{
		return (short) (number * 65535);
	}
	
	/**
	 * Converts a compressed fraction back to a double.
	 * @param compressed The compressed number
	 * @return The uncompressed double
	 */
	public static double convertShortToFraction(short compressed)
	{
		return (compressed & 0xFFFF) / 65535.0;
	}
	
	public static byte convertBooleansToByte(boolean... booleans)
	{
		if (booleans.length > 8)
		{
			throw new IllegalArgumentException("Limit 8 booleans, got " + booleans.length);
		}
		
		byte result = 0;
		for (int i = 0 ; i < booleans.length ; i++)
		{
			if (booleans[i])
			{
				result |= (1 << (7 - i));
			}
		}
		return result;
	}
	
	public static boolean[] convertByteToBooleans(byte value)
	{
		boolean[] booleans = new boolean[8];
		for (int i = 0 ; i < booleans.length ; i++)
		{
			booleans[i] = ((value >> (7 - i)) & 1) == 1;
		}
		return booleans;
	}
}
