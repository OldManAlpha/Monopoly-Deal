package oldmana.md.server.rules.struct;

import oldmana.md.server.rules.ValueType;

public interface JsonValue<T>
{
	ValueType<T> getValueType();
	
	T getDefaultValue();
}
