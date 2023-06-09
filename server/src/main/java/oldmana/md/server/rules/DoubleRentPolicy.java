package oldmana.md.server.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public enum DoubleRentPolicy
{
	ADD("Add", Double::sum),
	MULTIPLY("Multiply", (baseRent, currentRent) -> currentRent * 2);
	
	private static final Map<String, DoubleRentPolicy> jsonMap = new HashMap<String, DoubleRentPolicy>();
	static
	{
		for (DoubleRentPolicy type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	private final BiFunction<Integer, Double, Double> doubleFunction;
	
	DoubleRentPolicy(String jsonName, BiFunction<Integer, Double, Double> doubleFunction)
	{
		this.jsonName = jsonName;
		this.doubleFunction = doubleFunction;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public double doubleRent(int baseRent, double currentRent)
	{
		return doubleFunction.apply(baseRent, currentRent);
	}
	
	public static DoubleRentPolicy fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
