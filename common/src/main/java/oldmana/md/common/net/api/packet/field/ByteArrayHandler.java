package oldmana.md.common.net.api.packet.field;

import java.lang.reflect.Field;

import oldmana.md.common.net.api.MJPacketBuffer;

public class ByteArrayHandler extends FieldTypeHandler
{
	public ByteArrayHandler()
	{
		super(byte[].class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addBytes((byte[]) field.get(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getBytes());
	}
}
