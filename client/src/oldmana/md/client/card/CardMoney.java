package oldmana.md.client.card;

public class CardMoney extends Card
{
	public CardMoney(int id, int value)
	{
		super(id, value, "Money");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString()
	{
		return "Money Card (" + getValue() + ")";
	}
}
