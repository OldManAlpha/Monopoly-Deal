package oldmana.md.server.ai.util;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardTemplate;
import oldmana.md.server.card.CardType;

import java.util.concurrent.FutureTask;
import java.util.function.Function;

public class CardSnapshot
{
	private Card card;
	private CardTemplate template;
	
	public CardSnapshot(Card card)
	{
		this.card = card;
		template = card.getTemplate().clone();
	}
	
	public Card getCard()
	{
		return card;
	}
	
	public <V> V getSync(Function<Card, V> func)
	{
		try
		{
			FutureTask<V> task = new FutureTask<V>(() -> func.apply(getCard()));
			MDServer.getInstance().getSyncExecutor().execute(task);
			return task.get();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public CardTemplate getTemplate()
	{
		return template;
	}
	
	//@Override
	public int getID()
	{
		return 0;
	}
	
	//@Override
	public int getValue()
	{
		return 0;
	}
	
	//@Override
	public CardType<?> getType()
	{
		return null;
	}
}
