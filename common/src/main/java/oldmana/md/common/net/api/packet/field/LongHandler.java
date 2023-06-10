package oldmana.md.common.net.api.packet.field;

import java.lang.reflect.Field;

import oldmana.md.common.net.api.MJPacketBuffer;

public class LongHandler extends FieldTypeHandler
{
	public LongHandler()
	{
		super(long.class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addLong(field.getLong(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getLong());
	}
}
