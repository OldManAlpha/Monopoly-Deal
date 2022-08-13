package oldmana.general.mjnetworkingapi;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**A wrapper for the ByteBuffer class. Provides utilities to simplify adding common variables sent in packets.
 * 
 */
public class MJPacketBuffer
{
	private ByteBuffer bb;
	
	public MJPacketBuffer()
	{
		bb = ByteBuffer.allocate(256);
	}
	
	public MJPacketBuffer(int id, int size)
	{
		bb = ByteBuffer.allocate(6 + size);
		addShort((short) id);
		addInt(size);
	}
	
	public MJPacketBuffer(byte[] data)
	{
		bb = ByteBuffer.wrap(data);
	}
	
	public void addBoolean(boolean b)
	{
		try
		{
			bb.put((byte) (b ? 1 : 0));
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			addBoolean(b);
		}
	}
	
	public void addBooleans(boolean[] bs)
	{
		addInt(bs.length);
		for (boolean b : bs)
		{
			addByte((byte) (b ? 1 : 0));
		}
	}
	
	public boolean[] getBooleans()
	{
		int len = getInt();
		boolean[] bs = new boolean[len];
		for (int i = 0 ; i < len ; i++)
		{
			bs[i] = getByte() == 1;
		}
		return bs;
	}
	
	public boolean getBoolean()
	{
		return bb.get() == 1;
	}
	
	public void addByte(byte b)
	{
		try
		{
			bb.put(b);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 512);
			addByte(b);
		}
	}
	
	public byte getByte()
	{
		return bb.get();
	}
	
	public void addShort(short s)
	{
		try
		{
			bb.putShort(s);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 2);
			addShort(s);
		}
	}
	
	public short getShort()
	{
		return bb.getShort();
	}
	
	public void addInt(int i)
	{
		try
		{
			bb.putInt(i);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 4);
			addInt(i);
		}
	}
	
	public int getInt()
	{
		return bb.getInt();
	}
	
	public void addLong(long l)
	{
		try
		{
			bb.putLong(l);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 8);
			addLong(l);
		}
	}
	
	public long getLong()
	{
		return bb.getLong();
	}
	
	public void addFloat(float f)
	{
		try
		{
			bb.putFloat(f);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 4);
			addFloat(f);
		}
	}
	
	public float getFloat()
	{
		return bb.getFloat();
	}
	
	public void addDouble(double d)
	{
		try
		{
			bb.putDouble(d);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 8);
			addDouble(d);
		}
	}
	
	public double getDouble()
	{
		return bb.getDouble();
	}
	
	public void addChar(char c)
	{
		try
		{
			bb.putChar(c);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 2);
			addChar(c);
		}
	}
	
	public char getChar()
	{
		return bb.getChar();
	}
	
	public void addString(String s)
	{
		addInt(s.length());
		for (char c : s.toCharArray())
		{
			try
			{
				addChar(c);
			}
			catch (BufferOverflowException e)
			{
				resize(bb.capacity() + 2);
				addChar(c);
			}
		}
	}
	
	public String getString()
	{
		int len = getInt();
		String str = "";
		for (int i = 0 ; i < len ; i++)
		{
			str += getChar();
		}
		return str;
	}
	
	public void addStringArray(String[] strings)
	{
		addInt(strings.length);
		for (String s : strings)
		{
			addString(s);
		}
	}
	
	public String[] getStringArray()
	{
		int len = getInt();
		String[] strings = new String[len];
		for (int i = 0 ; i < len ; i++)
		{
			strings[i] = getString();
		}
		return strings;
	}
	
	public void addBytes(byte[] bs)
	{
		addInt(bs.length);
		append(bs);
	}
	
	public byte[] getBytes()
	{
		int len = getInt();
		byte[] bs = new byte[len];
		for (int i = 0 ; i < len ; i++)
		{
			bs[i] = getByte();
		}
		return bs;
	}
	
	public void addShorts(short[] ss)
	{
		addInt(ss.length);
		for (short s : ss)
		{
			addShort(s);
		}
	}
	
	public short[] getShorts()
	{
		int len = getInt();
		short[] ss = new short[len];
		for (int i = 0 ; i < len ; i++)
		{
			ss[i] = getShort();
		}
		return ss;
	}
	
	public void addInts(int[] is)
	{
		addInt(is.length);
		for (int i : is)
		{
			addInt(i);
		}
	}
	
	public int[] getInts()
	{
		int len = getInt();
		int[] is = new int[len];
		for (int i = 0 ; i < len ; i++)
		{
			is[i] = getInt();
		}
		return is;
	}
	
	public void addLongs(long[] ls)
	{
		addInt(ls.length);
		for (long l : ls)
		{
			addLong(l);
		}
	}
	
	public long[] getLongs()
	{
		int len = getInt();
		long[] ls = new long[len];
		for (int i = 0 ; i < len ; i++)
		{
			ls[i] = getLong();
		}
		return ls;
	}
	
	public void append(byte[] bytes)
	{
		try
		{
			if (bb.capacity() < bb.position() + bytes.length)
			{
				resize(bb.position() + bytes.length);
			}
			bb.put(bytes);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 512);
			append(bytes);
		}
	}
	
	public void trim()
	{
		resize(bb.position());
	}
	
	public void resize(int newCapacity)
	{
		ByteBuffer resized = ByteBuffer.allocate(newCapacity);
		resized.put(bb.array(), 0, bb.position());
		bb = resized;
		// TODO: bb.limit(newLimit)
	}
	
	public byte[] toByteArray()
	{
		trim();
		return bb.array();
	}
}
