package oldmana.md.server.rules;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum DiscardOrderPolicy
{
	MONEY_ACTION_FIRST("MoneyActionFirst", card -> !(card instanceof CardProperty)),
	PROPERTY_FIRST("PropertyFirst", card -> card instanceof CardProperty),
	ANY("Any", card -> true);
	
	private static final Map<String, DiscardOrderPolicy> jsonMap = new HashMap<String, DiscardOrderPolicy>();
	static
	{
		for (DiscardOrderPolicy type : values())
		{
			jsonMap.put(type.getJsonName(), type);
		}
	}
	
	private final String jsonName;
	private final Function<Card, Boolean> discardEvaluator;
	
	DiscardOrderPolicy(String jsonName, Function<Card, Boolean> discardEvaluator)
	{
		this.jsonName = jsonName;
		this.discardEvaluator = discardEvaluator;
	}
	
	public String getJsonName()
	{
		return jsonName;
	}
	
	public boolean canDiscard(Card card)
	{
		return discardEvaluator.apply(card);
	}
	
	public boolean canIgnorePolicy(Collection<Card> cards)
	{
		return cards.stream().noneMatch(card -> discardEvaluator.apply(card));
	}
	
	public static DiscardOrderPolicy fromJson(String jsonName)
	{
		return jsonMap.get(jsonName);
	}
}
