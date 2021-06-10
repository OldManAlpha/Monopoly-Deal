package oldmana.general.mjnetworkingapi.packet.field;

import java.lang.reflect.Field;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

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
