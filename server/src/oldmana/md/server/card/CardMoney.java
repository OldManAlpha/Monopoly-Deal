package oldmana.md.server.card;

public class CardMoney extends Card
{
	public CardMoney(int value)
	{
		super(value, value + "M");
		setDescription("Money can be banked to protect your properties from rent.");
	}
	
	@Override
	public String toString()
	{
		return "Money (" + getValue() + "M)";
	}
}
