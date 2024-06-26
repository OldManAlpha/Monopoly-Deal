package oldmana.md.server.rules;

import oldmana.md.server.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ValueType<T>
{
	private static final Map<Class<?>, ValueType<?>> types = new HashMap<Class<?>, ValueType<?>>();
	private static final Map<String, ValueType<?>> jsonMap = new HashMap<String, ValueType<?>>();
	
	private static final Set<String> VALID_TRUE = new HashSet<String>(Arrays.asList("true", "yes", "on", "enable", "enabled"));
	private static final Set<String> VALID_FALSE = new HashSet<String>(Arrays.asList("false", "no", "off", "disable", "disabled"));
	
	public static final ValueType<Integer> INTEGER = new ValueType<Integer>(Integer.class, "integer", input -> Integer.parseInt(input), null,
			ChatColor.FAINTLY_GRAY + "> Input a non-decimal number. Example: " + ChatColor.LIGHT_ORANGE + "2");
	public static final ValueType<Double> DOUBLE = new ValueType<Double>(Double.class, "number", input -> Double.parseDouble(input), null,
			ChatColor.FAINTLY_GRAY + "> Input a number. Example: " + ChatColor.LIGHT_ORANGE + "2.4");
	public static final ValueType<Boolean> BOOLEAN = new ValueType<Boolean>(Boolean.class, "boolean", input ->
			{
				input = input.toLowerCase();
				if (VALID_TRUE.contains(input))
				{
					return true;
				}
				else if (VALID_FALSE.contains(input))
				{
					return false;
				}
				throw new IllegalArgumentException("Invalid input");
			}, bool -> bool ? "Yes" : "No",
			ChatColor.FAINTLY_GRAY + "> Input \"" + ChatColor.LIGHT_ORANGE + "yes" + ChatColor.FAINTLY_GRAY + "\" or \"" +
					ChatColor.LIGHT_ORANGE + "no" + ChatColor.FAINTLY_GRAY + "\"");
	public static final ValueType<String> STRING = new ValueType<String>(String.class, "string", input -> input, null,
			ChatColor.FAINTLY_GRAY + "> Input any text");
	
	private Class<T> type;
	private String jsonName;
	private final Function<String, T> parser;
	private final Function<T, String> displayConverter;
	
	private final List<String> usage;
	
	private ValueType(Class<T> type, String jsonName, Function<String, T> parser, Function<T, String> displayConverter, String... usage)
	{
		this.type = type;
		this.jsonName = jsonName;
		this.parser = parser;
		this.displayConverter = displayConverter;
		types.put(type, this);
		jsonMap.put(jsonName, this);
		
		this.usage = Arrays.asList(usage);
	}
	
	public Class<T> getType()
	{
		return type;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public T parse(String input)
	{
		return parser.apply(input);
	}
	
	public String toDisplay(T input)
	{
		return displayConverter != null ? displayConverter.apply(input) : input.toString();
	}
	
	public boolean isCompatible(Object obj)
	{
		return obj.getClass() == type;
	}
	
	public List<String> getUsage()
	{
		return usage;
	}
	
	public static <C> ValueType<C> getByClass(Class<C> clazz)
	{
		return (ValueType<C>) types.get(clazz);
	}
	
	public static ValueType<?> getByJsonName(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
}
