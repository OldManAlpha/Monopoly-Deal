package oldmana.md.server.card.play.argument;

import oldmana.md.server.card.play.PlayArgument;

public class DiscardArgument implements PlayArgument
{
	public static final DiscardArgument INSTANCE = new DiscardArgument();
	
	private DiscardArgument() {}
}
