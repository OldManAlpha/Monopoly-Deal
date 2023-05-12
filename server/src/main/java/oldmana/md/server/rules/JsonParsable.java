package oldmana.md.server.rules;

/**
 * Interface for allowing users to input to modify a GameRule.
 */
public interface JsonParsable
{
	/**
	 * Takes the input and outputs a GameRule or primitive value.
	 * @param input User input
	 * @return A value corresponding to the input, or null if nothing matches
	 */
	Object parse(String input);
}
