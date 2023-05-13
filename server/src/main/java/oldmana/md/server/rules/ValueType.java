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
	private static Map<Class<?>, ValueType<?>> types = new HashMap<Class<?>, ValueType<?>>();
	
	private static final Set<String> VALID_TRUE = new HashSet<String>(Arrays.asList("true", "yes", "on", "enable", "enabled"));
	private static final Set<String> VALID_FALSE = new HashSet<String>(Arrays.asList("false", "no", "off", "disable", "disabled"));
	
	public static final ValueType<Integer> INTEGER = new ValueType<Integer>(Integer.class, input -> Integer.parseInt(input), null,
			ChatColor.FAINTLY_GRAY + "> Input a non-decimal number. Example: " + ChatColor.ORANGE + "2");
	public static final ValueType<Double> DOUBLE = new ValueType<Double>(Double.class, input -> Double.parseDouble(input), null,
			ChatColor.FAINTLY_GRAY + "> Input a number. Example: " + ChatColor.ORANGE + "2.4");
	public static final ValueType<Boolean> BOOLEAN = new ValueType<Boolean>(Boolean.class, input ->
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
			ChatColor.FAINTLY_GRAY + "> Input \"" + ChatColor.ORANGE + "yes" + ChatColor.FAINTLY_GRAY + "\" or \"" +
					ChatColor.ORANGE + "no" + ChatColor.FAINTLY_GRAY + "\"");
	public static final ValueType<String> STRING = new ValueType<String>(String.class, input -> input, null,
			ChatColor.FAINTLY_GRAY + "> Input any text");
	
	private Class<T> type;
	private final Function<String, T> parser;
	private final Function<T, String> displayConverter;
	
	private final List<String> usage;
	
	private ValueType(Class<T> type, Function<String, T> parser, Function<T, String> displayConverter, String... usage)
	{
		this.type = type;
		this.parser = parser;
		this.displayConverter = displayConverter;
		types.put(type, this);
		
		this.usage = Arrays.asList(usage);
	}
	
	public Class<T> getType()
	{
		return type;
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
}
