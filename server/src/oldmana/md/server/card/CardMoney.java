package oldmana.md.server.card;

public class CardMoney extends Card
{
	public CardMoney(int value)
	{
		super(value, "$" + value);
	}
	
	@Override
	public String toString()
	{
		return "Money (" + getValue() + "M)";
	}
}
