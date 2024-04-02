package oldmana.md.server.rules;

import oldmana.md.server.MDServer;
import oldmana.md.server.card.Card;

import java.util.function.Consumer;

public enum DiscardLocationPolicy implements JsonEnum
{
	TOP_OF_DISCARD("TopOfDiscard", card -> card.transfer(MDServer.getInstance().getDiscardPile())),
	BOTTOM_OF_DECK("BottomOfDeck", card -> card.transfer(MDServer.getInstance().getDeck())),
	RANDOMLY_IN_DECK("RandomlyInDeck", card -> MDServer.getInstance().getDeck().insertCardRandomly(card));
	
	private static final JsonEnumMapper<DiscardLocationPolicy> map = new JsonEnumMapper<DiscardLocationPolicy>(DiscardLocationPolicy.class);
	
	private final String jsonName;
	private final Consumer<Card> discarder;
	
	DiscardLocationPolicy(String jsonName, Consumer<Card> discarder)
	{
		this.jsonName = jsonName;
		this.discarder = discarder;
	}
	
	public void discard(Card card)
	{
		discarder.accept(card);
	}
	
	@Override
	public String getJsonName()
	{
		return jsonName;
	}
	
	public static DiscardLocationPolicy fromJson(String jsonName)
	{
		return map.fromJson(jsonName);
	}
	
	@Override
	public String toString()
	{
		return getJsonName();
	}
}
