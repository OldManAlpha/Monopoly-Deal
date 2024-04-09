package oldmana.md.client.card;

public class CardBuilding extends Card
{
	private int tier;
	private int rentAddition;
	
	public CardBuilding(int id, int value, String name, int tier, int rentAddition)
	{
		super(id, value, name);
		this.tier = tier;
		this.rentAddition = rentAddition;
	}
	
	public int getTier()
	{
		return tier;
	}
	
	public void setTier(int tier)
	{
		this.tier = tier;
	}
	
	public int getRentAddition()
	{
		return rentAddition;
	}
	
	public void setRentAddition(int rentAddition)
	{
		this.rentAddition = rentAddition;
	}
}
