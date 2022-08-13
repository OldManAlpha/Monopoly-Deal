package oldmana.md.server.card;

import oldmana.md.server.Player;

public class CardAction extends Card
{
	public CardAction(int value, String name)
	{
		super(value, name);
	}
	
	public void playCard(Player player) {}
	
	public boolean canPlayCard(Player player)
	{
		return true;
	}
	
	@Override
	public CardType getType()
	{
		return CardType.ACTION;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " (" + getValue() + "M)";
	}
}
