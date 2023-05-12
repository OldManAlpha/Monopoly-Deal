package oldmana.md.server.rules;

public interface JsonValue<T>
{
	ValueType<T> getValueType();
	
	T getDefaultValue();
}
