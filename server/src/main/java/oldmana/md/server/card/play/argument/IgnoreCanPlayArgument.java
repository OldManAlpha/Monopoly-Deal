package oldmana.md.server.card.play.argument;

import oldmana.md.server.card.play.PlayArgument;

public class IgnoreCanPlayArgument implements PlayArgument
{
	public static final IgnoreCanPlayArgument INSTANCE = new IgnoreCanPlayArgument();
	
	private IgnoreCanPlayArgument() {}
}
