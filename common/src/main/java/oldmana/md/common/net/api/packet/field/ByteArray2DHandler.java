package oldmana.md.common.net.api.packet.field;

import java.lang.reflect.Field;

import oldmana.md.common.net.api.MJPacketBuffer;

public class ByteArray2DHandler extends FieldTypeHandler
{
	public ByteArray2DHandler()
	{
		super(byte[][].class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		byte[][] bs = (byte[][]) field.get(packet);
		buffer.addInt(bs.length);
		for (int i = 0 ; i < bs.length ; i++)
		{
			buffer.addBytes(bs[i]);
		}
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		int len = buffer.getInt();
		byte[][] bs = new byte[len][];
		for (int i = 0 ; i < len ; i++)
		{
			bs[i] = buffer.getBytes();
		}
		field.set(packet, bs);
	}
}
