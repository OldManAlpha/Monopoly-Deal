package oldmana.md.server.card;

import oldmana.md.server.Player;

public class CardSpecial extends Card
{
	public CardSpecial(int value, String name)
	{
		super(value, name);
	}
	
	public void playCard(Player player, int data) {}
	
	@Override
	public CardType getType()
	{
		return CardType.SPECIAL;
	}
}
