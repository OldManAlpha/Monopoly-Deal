package oldmana.md.server.rules;

import java.util.function.BiFunction;

public enum DoubleRentPolicy implements JsonEnum
{
	ADD("Add", Double::sum),
	MULTIPLY("Multiply", (baseRent, currentRent) -> currentRent * 2);
	
	private static final JsonEnumMapper<DoubleRentPolicy> map = new JsonEnumMapper<DoubleRentPolicy>(DoubleRentPolicy.class);
	
	private final String jsonName;
	private final BiFunction<Integer, Double, Double> doubleFunction;
	
	DoubleRentPolicy(String jsonName, BiFunction<Integer, Double, Double> doubleFunction)
	{
		this.jsonName = jsonName;
		this.doubleFunction = doubleFunction;
	}
	
	@Override
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
		return map.fromJson(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
