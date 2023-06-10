package oldmana.md.common.net.api.packet.field;

import java.lang.reflect.Field;

import oldmana.md.common.net.api.MJPacketBuffer;

public class BooleanHandler extends FieldTypeHandler
{
	public BooleanHandler()
	{
		super(boolean.class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addBoolean(field.getBoolean(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getBoolean());
	}
}
