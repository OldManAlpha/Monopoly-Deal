package oldmana.md.server.card.play.argument;

import oldmana.md.server.card.play.PlayArgument;

/**
 * Silent plays do not print to the console.
 */
public class SilentPlayArgument implements PlayArgument
{
	public static final SilentPlayArgument INSTANCE = new SilentPlayArgument();
	
	private SilentPlayArgument() {}
}
