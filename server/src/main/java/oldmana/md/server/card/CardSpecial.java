package oldmana.md.server.card;

import oldmana.md.server.Player;
import oldmana.md.server.card.type.CardType;

public class CardSpecial extends Card
{
	public void playCard(Player player, int data) {}
	
	@Override
	public CardTypeLegacy getTypeLegacy()
	{
		return CardTypeLegacy.SPECIAL;
	}
	
	private static CardType<CardSpecial> createType()
	{
		return new CardType<CardSpecial>(CardSpecial.class, "Special Card", false);
	}
}
