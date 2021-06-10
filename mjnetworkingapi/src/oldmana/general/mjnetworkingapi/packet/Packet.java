package oldmana.general.mjnetworkingapi.packet;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import oldmana.general.mjnetworkingapi.MJConnection;
import oldmana.general.mjnetworkingapi.MJPacketBuffer;
import oldmana.general.mjnetworkingapi.packet.field.*;

/**The base class extended by all packets. Subclasses should override <i>fromBytes</i> and <i>toBytes</i>. A blank
 * constructor should always be present.
 *
 */
public abstract class Packet
{
	public static Map<Integer, Class<? extends Packet>> packets = new HashMap<Integer, Class<? extends Packet>>();
	public static Map<Class<? extends Packet>, Integer> packetIds = new HashMap<Class<? extends Packet>, Integer>();
	
	public static Map<Class<?>, FieldTypeHandler> typeHandlers = new HashMap<Class<?>, FieldTypeHandler>();
	
	static
	{
		// Primitive Types
		registerFieldTypeHandler(new BooleanHandler());
		registerFieldTypeHandler(new ByteHandler());
		registerFieldTypeHandler(new ShortHandler());
		registerFieldTypeHandler(new IntHandler());
		registerFieldTypeHandler(new LongHandler());
		registerFieldTypeHandler(new FloatHandler());
		registerFieldTypeHandler(new DoubleHandler());
		registerFieldTypeHandler(new CharHandler());
		
		// Primitive Array Types
		registerFieldTypeHandler(new BooleanArrayHandler());
		registerFieldTypeHandler(new ByteArrayHandler());
		registerFieldTypeHandler(new IntArrayHandler());
		
		// Primitive 2D Array Types
		registerFieldTypeHandler(new ByteArray2DHandler());
		
		// String
		registerFieldTypeHandler(new StringHandler());
		registerFieldTypeHandler(new StringArrayHandler());
	}
	
	public static int getID(Class<? extends Packet> clazz)
	{
		return packetIds.get(clazz);
	}
	
	/**Must be overridden by subclasses. Function is to restore a packet class state given the data.
	 * 
	 * @param data - Byte data
	 */
	//public abstract void fromBytes(MJPacketBuffer data);
	
	/**Must be overridden by subclasses. First must specify exact length, then puts fields into raw byte data. ID provided.
	 *  
	 * @return A representation of the packet in bytes.
	 */
	//public abstract byte[] toBytes(MJPacketBuffer data);
	
	
	public static Packet toPacket(int id, byte[] b)
	{
		try
		{
			MJPacketBuffer data = new MJPacketBuffer(b);
			Class<? extends Packet> c = packets.get(id);
			
			Packet p = c.newInstance();
			for (Field field : c.getDeclaredFields())
			{
				FieldTypeHandler handler = getFieldTypeHandler(field.getType());
				if (handler != null)
				{
					handler.fromBytes(field, p, data);
				}
			}
			//p.fromBytes(data);
			return p;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void toBytes(Packet p, MJPacketBuffer buffer)
	{
		int id = getID(p.getClass());
		try
		{
			Class<? extends Packet> c = packets.get(id);
			for (Field field : c.getDeclaredFields())
			{
				FieldTypeHandler handler = getFieldTypeHandler(field.getType());
				if (handler != null)
				{
					handler.toBytes(field, p, buffer);
				}
			}
			/*
			String s = "Packet: ";
			for (byte b : buffer.toByteArray())
			{
				if (b < 100)
				{
					s += "0";
				}
				if (b < 10)
				{
					s += "0";
				}
				s += b + " ";
			}
			System.out.println(s);
			*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Packet receivePackets(Socket s, int timeout) throws Exception
	{
		s.setSoTimeout(timeout);
		DataInputStream in = new DataInputStream(s.getInputStream());
		int id = in.readShort();
		int len = in.readInt();
		byte[] data = new byte[len];
		for (int i = 0 ; i < len ; i++)
		{
			data[i] = in.readByte();
		}
		
		return toPacket(id, data);
	}
	
	public static void sendPacket(Socket s, Packet p) throws Exception
	{
		OutputStream out = s.getOutputStream();
		MJPacketBuffer buffer = new MJPacketBuffer();
		buffer.addShort((short) getID(p.getClass()));
		MJPacketBuffer data = new MJPacketBuffer();
		toBytes(p, data);
		buffer.addInt(data.toByteArray().length);
		buffer.append(data.toByteArray());
		out.write(/*p.toBytes(buffer)*/buffer.toByteArray());
	}
	
	public static void sendPacket(MJConnection connection, Packet p) throws Exception
	{
		sendPacket(connection.getSocket(), p);
		/*
		OutputStream out = connection.getSocket().getOutputStream();
		MJPacketBuffer buffer = new MJPacketBuffer();
		buffer.addShort((short) getID(p.getClass()));
		MJPacketBuffer data = new MJPacketBuffer();
		toBytes(p, data);
		buffer.addInt(data.toByteArray().length);
		buffer.append(data.toByteArray());
		out.write(buffer.toByteArray());
		*/
	}
	
	public static int registerPacket(Class<? extends Packet> clazz)
	{
		int nextID = packets.size() + 1;
		packets.put(nextID, clazz);
		packetIds.put(clazz, nextID);
		return nextID;
	}
	
	public static Class<? extends Packet> getPacket(int ID)
	{
		return packets.get(ID);
	}
	
	public static void registerFieldTypeHandler(FieldTypeHandler handler)
	{
		typeHandlers.put(handler.getHandled(), handler);
	}
	
	public static FieldTypeHandler getFieldTypeHandler(Class<?> clazz)
	{
		return typeHandlers.get(clazz);
	}
}
