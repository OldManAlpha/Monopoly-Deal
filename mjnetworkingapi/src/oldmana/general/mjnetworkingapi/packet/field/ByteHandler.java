package oldmana.general.mjnetworkingapi.packet.field;

import java.lang.reflect.Field;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

public class ByteHandler extends FieldTypeHandler
{
	public ByteHandler()
	{
		super(byte.class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addByte(field.getByte(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffet) throws Exception
	{
		field.set(packet, buffet.getByte());
	}
}
