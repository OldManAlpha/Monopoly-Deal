package oldmana.general.mjnetworkingapi.packet.field;

import java.lang.reflect.Field;

import oldmana.general.mjnetworkingapi.MJPacketBuffer;

public abstract class FieldTypeHandler
{
	private Class<?> handled;
	
	public FieldTypeHandler(Class<?> clazz)
	{
		handled = clazz;
	}
	
	public Class<?> getHandled()
	{
		return handled;
	}
	
	public abstract void toBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception;
	
	public abstract void fromBytes(Field field, Object packet, MJPacketBuffer buffer) throws Exception;
}
