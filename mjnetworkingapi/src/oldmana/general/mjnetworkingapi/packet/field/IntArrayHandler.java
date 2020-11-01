package oldmana.general.mjnetworkingapi.packet.field;

import java.lang.reflect.Field;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

public class IntArrayHandler extends FieldTypeHandler
{
	public IntArrayHandler()
	{
		super(int[].class);
	}
	
	@Override
	public void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		buffer.addInts((int[]) field.get(packet));
	}
	
	@Override
	public void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception
	{
		field.set(packet, buffer.getInts());
	}
}
