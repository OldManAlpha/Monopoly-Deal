package oldmana.md.server.rules;

import java.util.HashMap;
import java.util.Map;

public class JsonEnumMapper<T extends Enum<T> & JsonEnum>
{
	private Map<String, T> jsonMap = new HashMap<String, T>();
	
	public JsonEnumMapper(Class<T> type)
	{
		try
		{
			for (T value : (T[]) type.getMethod("values").invoke(null))
			{
				jsonMap.put(value.getJsonName(), value);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public T fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
}
