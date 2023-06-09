package oldmana.md.client.card;

public class CardMoney extends Card
{
	public CardMoney(int id, int value, String name)
	{
		super(id, value, name);
	}
	
	@Override
	public String toString()
	{
		return "Money Card (" + getValue() + ")";
	}
}
