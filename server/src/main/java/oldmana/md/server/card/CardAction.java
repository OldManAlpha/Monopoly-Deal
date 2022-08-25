package oldmana.md.server.card;

import oldmana.md.server.Player;
import oldmana.md.server.card.type.CardType;

public class CardAction extends Card
{
	public CardAction() {}
	
	public void playCard(Player player) {}
	
	public boolean canPlayCard(Player player)
	{
		return true;
	}
	
	@Override
	public CardTypeLegacy getTypeLegacy()
	{
		return CardTypeLegacy.ACTION;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (" + getValue() + "M)";
	}
	
	private static CardType<CardAction> createType()
	{
		return new CardType<CardAction>(CardAction.class, "Action Card", false);
	}
}
