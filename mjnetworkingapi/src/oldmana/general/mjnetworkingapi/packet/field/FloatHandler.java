package oldmana.general.mjnetworkingapi.packet.field;

import java.lang.reflect.Field;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

public class FloatHandler extends FieldTypeHandler
{
	public FloatHandler()
	{
		super(float.class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addFloat(field.getFloat(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getFloat());
	}
}
