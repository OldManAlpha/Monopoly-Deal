package oldmana.md.server.card.play;

import oldmana.md.server.Player;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.collection.PropertySet;
import oldmana.md.server.card.play.argument.BankArgument;
import oldmana.md.server.card.play.argument.CardArgument;
import oldmana.md.server.card.play.argument.DiscardArgument;
import oldmana.md.server.card.play.argument.PlayerArgument;
import oldmana.md.server.card.play.argument.PropertySetArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayArguments
{
	/** Simple arguments that contain no arguments. Used for cards that don't require any parameters to play. **/
	public static final PlayArguments EMPTY = new PlayArguments();
	/** Simple arguments that can be used for playing a card into the bank. **/
	public static final PlayArguments BANK = new PlayArguments(BankArgument.INSTANCE);
	/** Simple arguments that can be used to discard a card. **/
	public static final PlayArguments DISCARD = new PlayArguments(DiscardArgument.INSTANCE);
	
	private List<PlayArgument> arguments;
	
	private PlayArguments(PlayArgument... args)
	{
		arguments = new ArrayList<PlayArgument>(Arrays.asList(args));
	}
	
	private PlayArguments(List<PlayArgument> args)
	{
		arguments = args;
	}
	
	public static PlayArguments of(PlayArgument... args)
	{
		return new PlayArguments(args);
	}
	
	public static PlayArguments of(List<PlayArgument> args)
	{
		return new PlayArguments(args);
	}
	
	public static PlayArguments ofPropertySet(PropertySet set)
	{
		return new PlayArguments(new PropertySetArgument(set));
	}
	
	public static PlayArguments ofPlayer(Player player)
	{
		return new PlayArguments(new PlayerArgument(player));
	}
	
	public static PlayArguments ofCard(Card card)
	{
		return new PlayArguments(new CardArgument(card));
	}
	
	public static PlayArguments ofCards(List<Card> cards)
	{
		return new PlayArguments(cards.stream().map(CardArgument::new).collect(Collectors.toList()));
	}
	
	public <T extends PlayArgument> T getArgument(Class<T> type)
	{
		for (PlayArgument argument : arguments)
		{
			if (argument.getClass() == type)
			{
				return (T) argument;
			}
		}
		return null;
	}
	
	public <T extends PlayArgument> List<T> getArguments(Class<T> type)
	{
		List<T> typeArguments = new ArrayList<T>();
		for (PlayArgument argument : arguments)
		{
			if (argument.getClass() == type)
			{
				typeArguments.add((T) argument);
			}
		}
		return typeArguments;
	}
	
	public List<PlayArgument> getArguments()
	{
		return Collections.unmodifiableList(arguments);
	}
	
	public boolean hasArgument(Class<? extends PlayArgument> type)
	{
		for (PlayArgument argument : arguments)
		{
			if (argument.getClass() == type)
			{
				return true;
			}
		}
		return false;
	}
	
	@SafeVarargs
	public final boolean hasAnyArgument(Class<? extends PlayArgument>... types)
	{
		for (PlayArgument argument : arguments)
		{
			for (Class<? extends PlayArgument> type : types)
			{
				if (argument.getClass() == type)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@SafeVarargs
	public final boolean hasAllArguments(Class<? extends PlayArgument>... types)
	{
		ArgIter:
		for (PlayArgument argument : arguments)
		{
			for (Class<? extends PlayArgument> type : types)
			{
				if (argument.getClass() == type)
				{
					continue ArgIter;
				}
			}
			return false;
		}
		return true;
	}
	
	public boolean isEmpty()
	{
		return arguments.isEmpty();
	}
}
