package oldmana.md.common.net.api.packet.field;

import java.lang.reflect.Field;

import oldmana.md.common.net.api.MJPacketBuffer;

public class BooleanArrayHandler extends FieldTypeHandler
{
	public BooleanArrayHandler()
	{
		super(boolean[].class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addBooleans((boolean[]) field.get(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getBooleans());
	}
}
