package oldmana.md.server.rules;

import oldmana.md.server.card.Card;
import oldmana.md.server.card.CardProperty;

import java.util.Collection;
import java.util.function.Function;

public enum DiscardOrderPolicy implements JsonEnum
{
	MONEY_ACTION_FIRST("MoneyActionFirst", card -> !(card instanceof CardProperty)),
	PROPERTY_FIRST("PropertyFirst", card -> card instanceof CardProperty),
	ANY("Any", card -> true);
	
	private static final JsonEnumMapper<DiscardOrderPolicy> map = new JsonEnumMapper<DiscardOrderPolicy>(DiscardOrderPolicy.class);
	
	private final String jsonName;
	private final Function<Card, Boolean> discardEvaluator;
	
	DiscardOrderPolicy(String jsonName, Function<Card, Boolean> discardEvaluator)
	{
		this.jsonName = jsonName;
		this.discardEvaluator = discardEvaluator;
	}
	
	public boolean canDiscard(Card card)
	{
		return discardEvaluator.apply(card);
	}
	
	public boolean canIgnorePolicy(Collection<Card> cards)
	{
		return cards.stream().noneMatch(card -> discardEvaluator.apply(card));
	}
	
	@Override
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static DiscardOrderPolicy fromJson(String jsonName)
	{
		return map.fromJson(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
